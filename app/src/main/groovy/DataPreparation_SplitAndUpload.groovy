import groovy.json.*
import com.mongodb.client.*
import org.bson.Document
import java.util.Properties

// ===========================================
// Data Preparation and Upload Script
// Splits CSV into 3 JSON subsets (30K, 60K, 90K)
// and uploads them into MongoDB Atlas.
// ===========================================

// ✅ Load credentials from properties file
def credFile = new File("src/main/resources/mongo.properties")
def cred = new Properties()

if (!credFile.exists()) {
    println "Properties file not found!"
    System.exit(1)
}

credFile.withInputStream { cred.load(it) }

def usn = cred.getProperty("USN")
def pwd = cred.getProperty("PWD")
def server = cred.getProperty("SERVER")
def dbName = cred.getProperty("DB")

// Construct MongoDB URI
def mongoUri = "mongodb+srv://${usn}:${pwd}@${server}.mongodb.net/?retryWrites=true&w=majority"

// Define input and output paths
def INPUT_CSV = "src/main/resources/anonymized_costs.csv"
def OUTPUT_DIR = "src/main/resources"

// ===========================================
// STEP 1️: Read CSV file
// ===========================================
def file = new File(INPUT_CSV)
if (!file.exists()) {
    println "CSV file not found at: ${file.absolutePath}"
    System.exit(1)
}

println "\n Reading CSV file..."
def reader = file.newReader("UTF-8")
def header = reader.readLine()?.split(",")
def allData = []

reader.eachLine { line ->
    def values = line.split(",")
    if (values.size() == header.size()) {
        def record = [:]
        for (int i = 0; i < header.size(); i++) {
            record[header[i].trim()] = values[i]?.trim()
        }
        allData << record
    }
}
reader.close()
println "Total records loaded from CSV: ${allData.size()}"

// ===========================================
// STEP 2️: Split into subsets (30K, 60K, 90K)
// ===========================================
def subsets = [
    [name: "30", size: 30000],
    [name: "60", size: 60000],
    [name: "90", size: 90000]
]

def jsonFiles = []

subsets.each { subset ->
    def subsetData = allData.take(subset.size)
    def outputFile = new File("${OUTPUT_DIR}/anonymized_costs_${subset.name}.json")
    def jsonContent = JsonOutput.prettyPrint(JsonOutput.toJson(subsetData))
    outputFile.write(jsonContent, "UTF-8")
    jsonFiles << [name: subset.name, file: outputFile, data: subsetData]
    println "JSON file created: ${outputFile.name} with ${subsetData.size()} records"
}

// ===========================================
// STEP 3️: Upload each subset to MongoDB
// ===========================================
println "\n Connecting to MongoDB Atlas..."
try {
    def mongoClient = MongoClients.create(mongoUri)
    def database = mongoClient.getDatabase(dbName)

    jsonFiles.each { subset ->
        def collectionName = "azurecosts_${subset.name}"
        def collection = database.getCollection(collectionName)
        println "\n Uploading ${subset.data.size()} records to collection '${collectionName}'..."

        collection.drop() // clear old data if present
        def documents = subset.data.collect { new Document(it) }
        collection.insertMany(documents)

        println "Successfully uploaded ${documents.size()} records to ${collectionName}"
    }

    mongoClient.close()
    println "\n All uploads completed successfully!"
} catch (Exception e) {
    println "Error during MongoDB upload: ${e.message}"
}

// ===========================================
// STEP 4️: Create upload summary file
// ===========================================
def summary = jsonFiles.collect {
    [
        FileName: it.file.name,
        Records: it.data.size(),
        MongoCollection: "azurecosts_${it.name}"
    ]
}

def summaryFile = new File("${OUTPUT_DIR}/output/upload_summary.json")
if (!summaryFile.parentFile.exists()) summaryFile.parentFile.mkdirs()
summaryFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(summary))

println "\n Upload Summary saved to: ${summaryFile.absolutePath}"
println "==========================================================="
println " Task Completed Successfully"
println "==========================================================="
