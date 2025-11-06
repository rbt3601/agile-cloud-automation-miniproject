import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.text.SimpleDateFormat

// ===========================================
// 🧩 Local Scalability Analysis (Ex5_1)
// ===========================================

// List of dataset file names to test
def datasets = [
    "src/main/resources/anonymized_costs_30.json",
    "src/main/resources/anonymized_costs_60.json",
    "src/main/resources/anonymized_costs_90.json"
]

// Store results
def results = []

// Loop through each dataset
datasets.each { filePath ->

    println "\n---------------------------------------------------------------"
    println "Starting Analysis for Dataset: ${filePath}"
    println "---------------------------------------------------------------"

    def startTime = System.nanoTime()

    def file = new File(filePath)
    if (!file.exists()) {
        println "File not found: ${file.absolutePath}"
        return
    }

    // Parse JSON into Groovy List
    def conjson = new JsonSlurper().parse(file)
    println "${conjson.size()} records."

    // Define date range
    def dateformat = new SimpleDateFormat("dd/MM/yyyy")
    def startdate = dateformat.parse("22/12/2022")
    def enddate = dateformat.parse("22/03/2023")

    // Filter and transform
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

    // Group by ServiceName and Region
    def groupeddata = pipeline.groupBy { row -> [row.ServiceName, row.Region] }

    // Aggregate total and average cost per group
    def aggregateddata = groupeddata.collectEntries { key, rows ->
        def totalcost = rows.sum { it.Cost.toBigDecimal() }
        def avgcost = totalcost / rows.size()
        [key, [totalcost: totalcost, avgcost: avgcost]]
    }

    // Sort descending by total cost
    def sorteddata = aggregateddata.sort { -it.value.totalcost }

    // Print top results
    println "\n================== Aggregated Cost Summary =================="
    printf("%-30s %-20s %-15s %-15s\n", "Service Name", "Region", "Total Cost", "Average Cost")
    println "--------------------------------------------------------------------------"

    sorteddata.take(10).each { key, value ->
        def (service, region) = key
        printf("%-30s %-20s %-15.2f %-15.2f\n", service, region, value.totalcost, value.avgcost)
    }

    def topservice = sorteddata.take(1)
    println "==========================================================================="
    println "Top Service by Cost: ${topservice.keySet().first()} - ${topservice.values().first()}"
    println "==========================================================================="

    // Measure total execution time
    def endTime = System.nanoTime()
    def elapsed = (endTime - startTime) / 1_000_000_000.0
    println String.format("Total Execution Time for %s: %.3f seconds", file.name, elapsed)

    // Save summary for results table
    results << [
        Dataset: file.name,
        Records: conjson.size(),
        ExecutionTimeSec: elapsed
    ]

    // Save output for each dataset
    def outputDir = new File("src/main/resources/output")
    if (!outputDir.exists()) outputDir.mkdirs()

    def aggregatedjson = JsonOutput.prettyPrint(JsonOutput.toJson(sorteddata.take(10)))
    new File(outputDir, "summary_${file.name.replace('.json', '')}.json").write(aggregatedjson, "UTF-8")
}

// ===========================================
// 📊 Summary of All Datasets
// ===========================================
println "\n================== LOCAL SCALABILITY SUMMARY =================="
printf("%-20s %-15s %-15s\n", "Dataset", "Records", "Exec Time (s)")
println "---------------------------------------------------------------"
results.each { r ->
    printf("%-20s %-15d %-15.3f\n", r.Dataset, r.Records, r.ExecutionTimeSec)
}
println "================================================================"

def outputFile = new File("src/main/resources/output/local_scalability_summary.json")
outputFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(results))
println "\n Local scalability summary saved in: ${outputFile.absolutePath}"
