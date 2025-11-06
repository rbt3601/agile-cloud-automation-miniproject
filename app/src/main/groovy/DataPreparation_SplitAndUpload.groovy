import groovy.json.*
import com.mongodb.client.*
import org.bson.Document
import java.util.Properties

//Load credentials from properties file
def credFile = new File("src/main/resources/mongo.properties")
def cred = new Properties()

if (!credFile.exists()) {
    println "Properties file not found!"
    System.exit(1)
}

// Load the properties
credFile.withInputStream { cred.load(it) }

//Extract credentials
def usn = cred.getProperty("USN")
def pwd = cred.getProperty("PWD")
def server = cred.getProperty("SERVER")
def dbName = cred.getProperty("DB")
def collectionName = cred.getProperty("COLLECTION")

//Construct MongoDB URI dynamically
def mongoUri = "mongodb+srv://${usn}:${pwd}@${server}.mongodb.net/?retryWrites=true&w=majority"


//Define dataset input/output paths
def INPUT_CSV = "src/main/resources/anonymized_costs.csv"
def OUTPUT_JSON = "src/main/resources/anonymized_costs_90.json"

//Read and convert CSV to JSON (first 40,000 records)
def file = new File(INPUT_CSV)
if (!file.exists()) {
    println "CSV file not found at: ${file.absolutePath}"
    System.exit(1)
}

println "Reading CSV file..."
def reader = file.newReader("UTF-8")
def header = reader.readLine()?.split(",")
def dataList = []

int lineCount = 0
reader.eachLine { line ->
    if (lineCount >= 90000) return
    if (lineCount > 0) {
        def values = line.split(",")
        def record = [:]
        for (int i = 0; i < header.size(); i++) {
            record[header[i].trim()] = values[i]?.trim()
        }
        dataList << record
    }
    lineCount++
}
reader.close()

println "Loaded ${dataList.size()} records from CSV."

// ✅ Step 6: Write subset to JSON file
println "Writing subset to JSON file..."
def jsonContent = JsonOutput.prettyPrint(JsonOutput.toJson(dataList))
new File(OUTPUT_JSON).write(jsonContent, "UTF-8")
println "JSON file created: ${OUTPUT_JSON}"

// ✅ Step 7: Upload data to MongoDB Atlas
println "\n Connecting to MongoDB Atlas and uploading data..."
try {
    def mongoClient = MongoClients.create(mongoUri)
    def database = mongoClient.getDatabase(dbName)
    def collection = database.getCollection(collectionName)

    println "Uploading records to collection '${collectionName}'..."
    def documents = dataList.collect { new Document(it) }

    collection.insertMany(documents)
    println "Successfully uploaded ${documents.size()} records to MongoDB Atlas."

    mongoClient.close()
} catch (Exception e) {
    println "Error during MongoDB upload: ${e.message}"
}

println "\n Task completed successfully!"
