import com.mongodb.client.*
import org.bson.Document
import java.text.SimpleDateFormat
import groovy.json.JsonOutput

// =============================================================
// Exercise 5_2 - Cloud Scalability Analysis (MongoDB Atlas)
// Runs aggregation on azurecosts_30, azurecosts_60, azurecosts_90
// =============================================================

// Load credentials from mongo.properties
def properties = new Properties()
def propertiesFile = new File("src/main/resources/mongo.properties")
if (!propertiesFile.exists()) {
    println "mongo.properties file not found!"
    System.exit(1)
}
propertiesFile.withInputStream { properties.load(it) }

// Build MongoDB URI
def mongoUri = "mongodb+srv://${properties.USN}:${properties.PWD}@${properties.SERVER}.mongodb.net/${properties.DB}?retryWrites=true&w=majority"

// Connect to MongoDB
def mongoClient = MongoClients.create(mongoUri)
def db = mongoClient.getDatabase(properties.DB)

println "\n Connected to MongoDB Atlas Database: ${db.getName()}"
println "------------------------------------------------------------"

// MongoDB collections (datasets)
def collections = ["azurecosts_30", "azurecosts_60", "azurecosts_90"]

// Common date filter range
def sdf = new SimpleDateFormat("MM/dd/yyyy")
def start = sdf.parse("12/22/2022")
def end = sdf.parse("03/22/2023")

// Define common pipeline
def addDate = new Document("\$addFields",
  new Document("parsedDate",
    new Document("\$dateFromString",
      new Document("dateString", "\$Date").append("format","%m/%d/%Y")
    )
  )
)
def date_filteration = new Document("\$match",
  new Document("parsedDate", new Document("\$gte", start).append("\$lte", end))
)
def pipeline = [
    addDate,
    date_filteration,
    new Document("\$project",
      new Document("_id", 0)
        .append("Date", 1)
        .append("ServiceName", "\$MeterCategory")
        .append("Region", "\$ResourceLocation")
        .append("Cost",
          new Document("\$toDouble",
            new Document("\$ifNull", ["\$CostInBillingCurrency", "0"])
          )
        )
    ),
    new Document("\$group",
      new Document("_id",
        new Document("ServiceName", "\$ServiceName")
          .append("Region", "\$Region")
      )
      .append("TotalCost",   new Document("\$sum", "\$Cost"))
      .append("AverageCost", new Document("\$avg", "\$Cost"))
      .append("Count",       new Document("\$sum", 1))
    ),
    new Document("\$sort", new Document("TotalCost", -1)),
    new Document("\$limit", 10)
]

// Store overall results for summary
def summaryResults = []

// =============================================================
// Run aggregation for each collection
// =============================================================
collections.each { cname ->
    println "\n------------------------------------------------------------"
    println "Running Aggregation on Collection: ${cname}"
    println "------------------------------------------------------------"

    def col = db.getCollection(cname)

    // Count total documents in this collection
    def totalDocs = col.countDocuments()
    println "📦 Records in ${cname}: ${totalDocs}"

    // Start timer
    def startTime = System.nanoTime()
    def aggResults = []
    col.aggregate(pipeline).into(aggResults)
    def endTime = System.nanoTime()
    def elapsed = (endTime - startTime) / 1_000_000_000.0

    // Print output table
    println "\n================== Aggregated Cost Summary =================="
    printf("%-30s %-20s %-15s %-15s\n", "Service Name", "Region", "Total Cost", "Average Cost")
    println "--------------------------------------------------------------------------"
    aggResults.each { d ->
        def k = d.get("_id") as Document
        def svc = (k.get("ServiceName") ?: "").toString()
        def reg = (k.get("Region") ?: "").toString()
        def tot = (d.get("TotalCost") as Number).doubleValue()
        def avg = (d.get("AverageCost") as Number).doubleValue()
        printf("%-30s %-20s %-15.2f %-15.2f\n", svc, reg, tot, avg)
    }

    // Get top service
    def top = aggResults[0]
    def topService = top?._id?.ServiceName ?: "N/A"
    def topRegion = top?._id?.Region ?: "N/A"
    def topTotal = top?.TotalCost ?: 0

    println "==========================================================================="
    println String.format("Top Service: %s (%s) - Total Cost: %.2f", topService, topRegion, topTotal)
    println String.format("Execution Time for %s: %.3f seconds", cname, elapsed)
    println "==========================================================================="

    // Save output to JSON
    def outDir = new File("src/main/resources/output")
    if (!outDir.exists()) outDir.mkdirs()
    def outFile = new File(outDir, "cloud_summary_${cname}.json")
    outFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(aggResults))

    // Add result to summary list
    summaryResults << [
        Collection: cname,
        Documents: totalDocs,
        TopService: topService,
        Region: topRegion,
        TotalCost: String.format("%.2f", topTotal),
        ExecutionTimeSec: String.format("%.3f", elapsed)
    ]
}

// =============================================================
// Print overall scalability summary
// =============================================================
println "\n================== CLOUD SCALABILITY SUMMARY =================="
printf("%-20s %-12s %-25s %-15s %-15s %-15s\n",
       "Collection", "Records", "Top Service", "Region", "Total Cost", "Exec Time(s)")
println "-------------------------------------------------------------------------------------------"
summaryResults.each { r ->
    printf("%-20s %-12d %-25s %-15s %-15s %-15s\n",
           r.Collection, r.Documents, r.TopService, r.Region, r.TotalCost, r.ExecutionTimeSec)
}
println "==========================================================================================="

// Save summary JSON file
def summaryFile = new File("src/main/resources/output/cloud_scalability_summary.json")
summaryFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(summaryResults))

println "\n Cloud scalability summary saved in: ${summaryFile.absolutePath}"

// Close MongoDB connection
mongoClient.close()
println "\n Mongo client closed successfully."
