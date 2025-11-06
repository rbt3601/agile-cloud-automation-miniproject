import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.text.SimpleDateFormat


// ✅ Start execution timer
def startTime = System.nanoTime()

// ✅ Read data directly from JSON file
def file = new File("src/main/resources/anonymized_costs_90.json")   // change name if needed
if (!file.exists()) {
    println "❌ JSON file not found at ${file.absolutePath}"
    System.exit(1)
}

// Parse JSON into Groovy list of maps
def conjson = new groovy.json.JsonSlurper().parse(file)
println "✅ Loaded ${conjson.size()} records from JSON."


def dateformat = new SimpleDateFormat("dd/MM/yyyy") // this specifies the date format like dd/mm/yyyy
def startdate = dateformat.parse("22/12/2022") // this specifies the start date
def enddate = dateformat.parse("22/03/2023") // this specifies the end date

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

// ✅ Measure total execution time
def endTime = System.nanoTime()
def elapsed = (endTime - startTime) / 1_000_000_000.0
println String.format("⏱️  Total Execution Time: %.3f seconds", elapsed)

