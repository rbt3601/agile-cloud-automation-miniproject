import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.text.SimpleDateFormat

// === Read CSV ===
def file = new File("/Users/akshay/Documents/Projects/agile-cloud-automation-miniproject/app/src/main/resources/anonymized_costs.csv")
def reader = file.newReader()
def csvdata = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())

// === Convert CSV rows to map ===
def csvmaps = csvdata.collect { row ->
    [
        Date: row.get("Date"),
        ConsumedService: row.get("ConsumedService"),
        ResourceLocation: row.get("ResourceLocation"),
        CostInBillingCurrency: row.get("CostInBillingCurrency")
    ]
}

// === Convert to JSON & back (optional, keeps original logic) ===
def jsondata = JsonOutput.toJson(csvmaps)
def conjson = new JsonSlurper().parseText(jsondata)

// === Filter by date range ===
def dateformat = new SimpleDateFormat("dd/MM/yyyy")
def startdate = dateformat.parse("22/12/2022")
def enddate = dateformat.parse("22/01/2023")

def pipeline = conjson.findAll { row ->
    def rowdate = dateformat.parse(row.Date)
    rowdate >= startdate && rowdate <= enddate
}.collect { row ->
    [
        Date: row.Date,
        ServiceName: row.ConsumedService,
        Region: row.ResourceLocation,
        Cost: row.CostInBillingCurrency
    ]
}

// === Group by Service and Region ===
def groupeddata = pipeline.groupBy { row -> [row.ServiceName, row.Region] }

// === Aggregate (total & average cost) ===
def aggregateddata = groupeddata.collectEntries { key, rows ->
    def totalcost = rows.sum { it.Cost.toBigDecimal() }
    def avgcost = totalcost / rows.size()
    [key, [totalcost: totalcost, avgcost: avgcost]]
}

// === Sort by total cost descending ===
def sorteddata = aggregateddata.sort { -it.value.totalcost }
def topservice = sorteddata.take(1)

// === 1️⃣ Print table to console ===
println "\n================== Aggregated Cost Summary =================="
printf("%-30s %-20s %-15s %-15s\n", "Service Name", "Region", "Total Cost", "Average Cost")
println "--------------------------------------------------------------------------"

sorteddata.take(10).each { key, value ->
    def (service, region) = key
    printf("%-30s %-20s %-15.2f %-15.2f\n", service, region, value.totalcost, value.avgcost)
}

println "==========================================================================="
println "\nTop Service by Cost: ${topservice}\n"

// === 2️⃣ Save JSON to file ===
def outputDir = new File("src/main/resources/output")
if (!outputDir.exists()) outputDir.mkdirs() // create directory if missing

def aggregatedjson = JsonOutput.toJson(aggregateddata)
new File(outputDir, "summary_costs.json").write(JsonOutput.prettyPrint(aggregatedjson))

println "Aggregated summary saved successfully in ${outputDir.absolutePath}/summary_costs.json"
