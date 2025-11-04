import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.text.SimpleDateFormat


def file = new File("/Users/akshay/Documents/Projects/agile-cloud-automation-miniproject/app/src/main/resources/anonymized_costs.csv") //This points towards my csv
def reader = file.newReader() //this opens the file for reading
def csvdata = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()) //this parses the csv file and considers the first row as heading

// This iterates through and creates a list for each row
def csvmaps = csvdata.collect { row ->
    [
        Date: row.get("Date"),
        ConsumedService: row.get("ConsumedService"),
        ResourceLocation: row.get("ResourceLocation"),
        CostInBillingCurrency: row.get("CostInBillingCurrency")
    ]
}

// This converts the groovy objects into json and then parses it back to groovy objects
def jsondata = JsonOutput.toJson(csvmaps)
def conjson = new JsonSlurper().parseText(jsondata)


def dateformat = new SimpleDateFormat("dd/MM/yyyy") // this specifies the date format like dd/mm/yyyy
def startdate = dateformat.parse("22/12/2022") // this specifies the start date
def enddate = dateformat.parse("22/01/2023") // this specifies the end date

//findall filters the data between the specified dates and creates a new list pipeline
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


def groupeddata = pipeline.groupBy { row -> [row.ServiceName, row.Region] } //this groups the data by service name and region

// this aggregates the total and average cost for each service and region
def aggregateddata = groupeddata.collectEntries { key, rows ->
    def totalcost = rows.sum { it.Cost.toBigDecimal() }  //big decimal represents decimal numbers and stores the digit exactly
    def avgcost = totalcost / rows.size()
    [key, [totalcost: totalcost, avgcost: avgcost]]
}


def sorteddata = aggregateddata.sort { -it.value.totalcost } //this sorts the data in descending order based on total cost
def topservice = sorteddata.take(1)


println "\n================== Aggregated Cost Summary =================="
printf("%-30s %-20s %-15s %-15s\n", "Service Name", "Region", "Total Cost", "Average Cost")
println "--------------------------------------------------------------------------"

sorteddata.take(10).each { key, value ->
    def (service, region) = key
    printf("%-30s %-20s %-15.2f %-15.2f\n", service, region, value.totalcost, value.avgcost)
}

println "==========================================================================="
println "\nTop Service by Cost: ${topservice}\n"


def outputDir = new File("src/main/resources/output")
if (!outputDir.exists()) outputDir.mkdirs() // create directory if missing

def aggregatedjson = JsonOutput.toJson(aggregateddata) //converts objects back to json
new File(outputDir, "summary_costs.json").write(JsonOutput.prettyPrint(aggregatedjson)) //this writes the jasond data to a file

println "Aggregated summary saved successfully in ${outputDir.absolutePath}/summary_costs.json"
