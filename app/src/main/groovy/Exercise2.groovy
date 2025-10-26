import org.apache.commons.csv.CSVFormat // the csv format defines how the csv file is structured
import org.apache.commons.csv.CSVParser //csv parser is used to read or parse the csv file
import groovy.json.JsonOutput  // to convert groovy objects
import groovy.json.JsonSlurper // to read json data and convert into groovy objects
import java.text.SimpleDateFormat //this lets us to parse the date in spcific format



def file = new File("/Users/akshay/Documents/Projects/agile-cloud-automation-miniproject/app/src/main/resources/anonymized_costs.csv") // this file points towards my dataset
//def csvdata = new CsvParser().parse(file) // read the data and converts into groovy objects
def reader = file.newReader() // creates a reader object to read the file(reads the file line by line)
def csvdata = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()) // CSVFormat.DEFAULT this uses the deafult rules comma seperated standard quoting withFirstRecordAsHeader ignores the first row as “data” and treats it as column names (headers so thst we can access the columns by names).

def csvmaps = csvdata.collect { row ->
    [
        Date: row.get("Date"),
        ConsumedService: row.get("ConsumedService"),
        ResourceLocation: row.get("ResourceLocation"),
        CostInBillingCurrency: row.get("CostInBillingCurrency") // this loops throough each rows converts csv into map with key value pairs
    ]
}

def jsondata = JsonOutput.toJson(csvmaps) //converts the objects ointo json
def conjson = new JsonSlurper().parseText(jsondata) //reads the json data and converts into groovy obj(lists)

def dateformat = new SimpleDateFormat("dd/MM/yyyy") // this tells groovy how the date is formatted
def startdate = dateformat.parse("22/12/2022") // this is the start date where well perform aggregation from 22nd
def enddate = dateformat.parse("22/01/2023") // end date
//find all loops through the data and keeps only the data with the following condition 
def pipeline = conjson.findAll { 
    row -> 
     def rowdate = dateformat.parse(row.Date)
     rowdate >= startdate && rowdate <=  enddate
}
.collect { // for every filter passed this creates a list of date servicename region and cost
    row -> 
    [
        Date : row.Date,
        ServiceName : row.ConsumedService,
        Region : row.ResourceLocation,
        Cost :row.CostInBillingCurrency
    ]

}

//once we ahve filtered the data we will group the data by service name and region
// ive grouped the data into service name and region
def groupeddata = pipeline.groupBy { 
    row -> [row.ServiceName, row.Region] 
    }

//total and average cost per group

def aggregateddata = groupeddata.collectEntries { key, rows ->
def totalcost = rows.sum {it.Cost.toBigDecimal()} // normally the data is read as strings "85.6" toBigDecimal converts into numeric value so that aggregagtion could be performed
def avgcost = totalcost / rows.size()
[key, [totalcost:totalcost, avgcost:avgcost]] // transforms the data into map
}

def sorteddata = aggregateddata.sort{-it.value.totalcost}
def topservice = sorteddata.take(1)
println topservice

// Define output directory with 'def'
def outputDir = new File("src/main/resources/output")
if (!outputDir.exists()) outputDir.mkdirs() // create if missing

// Convert Groovy objects to JSON strings
def filteredjson = JsonOutput.toJson(pipeline)
def aggregatedjson = JsonOutput.toJson(aggregateddata)

// Write pretty-printed JSON files
new File(outputDir, "anonymized_costs_filtered.json").write(JsonOutput.prettyPrint(filteredjson))
new File(outputDir, "summary_costs.json").write(JsonOutput.prettyPrint(aggregatedjson))

println "files created successfully in ${outputDir.absolutePath}"