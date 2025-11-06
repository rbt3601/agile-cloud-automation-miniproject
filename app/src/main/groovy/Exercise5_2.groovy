import com.mongodb.client.*
import org.bson.Document
import java.text.SimpleDateFormat
import groovy.json.JsonOutput

// Created the properties object so that it could read key value pairs from the mongo.properties file
def properties = new Properties()

//Created the File object because we need to pass the mongo.properties file location so it points towards the actual mongo.properties file
def propertiesFile = new File('src/main/resources/mongo.properties')
// We are loading the mongo.properties file inside the properties object
propertiesFile.withInputStream { properties.load(it) }

// Connecting to the mongoDB database using MongoClients.create method and passing the values from mongo.properties file
def mongoClient = MongoClients.create(
    "mongodb+srv://${properties.USN}:${properties.PWD}@${properties.SERVER}.mongodb.net/${properties.DB}?retryWrites=true&w=majority"
)
// Fetching the database object from the connected client
def db  = mongoClient.getDatabase(properties.DB)

// Fetching the database collection object from the connected client
def col = db.getCollection(properties.COLLECTION as String)

println "database: ${db.getName()}"
db.listCollectionNames().each { println it }

/* Setting date logic for filtering the dataset */
def sdf   = new SimpleDateFormat("MM/dd/yyyy")
def start = sdf.parse("12/22/2022")
def end   = sdf.parse("03/22/2023")

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

def pipeLine_sortingByCost = [
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


//---------------------------------------------
// 🕒 Step: Measure Aggregation Execution Time
//---------------------------------------------
println "\n▶ Running MongoDB Aggregation Pipeline..."
def startTime = System.nanoTime()

def aggResults = []
col.aggregate(pipeLine_sortingByCost).into(aggResults)

def endTime = System.nanoTime()
def elapsedTime = (endTime - startTime) / 1_000_000_000.0
//---------------------------------------------

//Printing formatted table header
println "ServiceName                   | Region        | TotalCost      | AverageCost    | Count"
println "------------------------------------------------------------------------------------------"

aggResults.each { d ->
    def k = d.get("_id") as Document
    def svc = (k.get("ServiceName") ?: "").toString()
    def reg = (k.get("Region") ?: "").toString()
    def tot = (d.get("TotalCost") as Number).doubleValue()
    def avg = (d.get("AverageCost") as Number).doubleValue()
    def cnt = d.get("Count")
    println "${svc.padRight(28)} | ${reg.padRight(12)} | ${String.format('%.6f', tot).padRight(13)} | ${String.format('%.6f', avg).padRight(13)} | ${cnt}"
}

//Here we are extracting the same output in JSON format and saving it into a file
def rows = aggResults.collect { d ->
    def k = d.get("_id") as Document
    def svc = (k.get("ServiceName") ?: "").toString()
    def reg = (k.get("Region") ?: "").toString()
    def tot = (d.get("TotalCost") as Number).doubleValue()
    def avg = (d.get("AverageCost") as Number).doubleValue()
    def cnt = d.get("Count")
    [ServiceName: svc, Region: reg, TotalCost: tot, AverageCost: avg, Count: cnt]
}

def outDir = new File("${System.getProperty('user.dir')}/src/main/resources/output")
outDir.mkdirs()
def jsonFile = new File(outDir, "Exercise-3.json")
jsonFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(rows))

//---------------------------------------------
// ⏱️ Step: Print Aggregation Time Summary
//---------------------------------------------
println "\n==========================================================================="
println String.format("✅ MongoDB Aggregation Completed in %.3f seconds", elapsedTime)
println "==========================================================================="

// Close connection
mongoClient.close()
println "Mongo client closed"
