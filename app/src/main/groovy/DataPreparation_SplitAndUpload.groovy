/**
 * Extra Task – Split Large CSV and Upload Partial JSON to Cloud
 *
 * Task:
 * -----
 * You are given a large CSV file (~96,000 records) containing Azure cost data.
 * The goal is to create a Groovy script that:
 *
 * 1. Reads the large CSV file from "src/main/resources/anonymized_costs_large.csv".
 * 2. Selects only the first 40,000 records (excluding header).
 * 3. Converts those records into JSON format.
 * 4. Saves the JSON file locally as:
 *        src/main/resources/anonymized_costs_40k.json
 * 5. Uploads the JSON file to your MongoDB Atlas cloud database
 *    (into the "cloudcosts" database, collection "azure_cost_data_40k").
 *
 * ---------------
 * Functional Requirements:
 * ---------------
 * - Use efficient reading (BufferedReader or File.eachLine()) to avoid memory overload.
 * - Ensure proper JSON formatting using groovy.json.JsonOutput.
 * - Handle headers properly and maintain key-value mapping.
 * - Use MongoDB Java Driver (within Groovy) to connect and upload data.
 * - Implement basic exception handling for file and database operations.
 *
 * ---------------
 * Steps to Implement:
 * ---------------
 * 1. Open and read the CSV file line by line.
 * 2. Parse each line into a Map using the header row for keys.
 * 3. Stop after 40,000 data lines.
 * 4. Add all maps into a List and convert to JSON using:
 *        JsonOutput.prettyPrint(JsonOutput.toJson(list))
 * 5. Write the JSON string to a local file.
 * 6. Connect to MongoDB Atlas using connection string:
 *        mongodb+srv://<username>:<password>@<cluster-url>/cloudcosts
 * 7. Insert the JSON records into the "azure_cost_data_40k" collection.
 * 8. Print confirmation messages after file creation and upload success.
 *
 * ---------------
 * Objective:
 * ---------------
 * To practice handling large datasets efficiently by limiting records locally
 * and integrating Groovy data processing with cloud database upload.
 *
 * ---------------
 * Output:
 * ---------------
 * - Local file: anonymized_costs_40k.json (40,000 records)
 * - MongoDB collection: azure_cost_data_40k (uploaded records)
 *
 * ---------------
 * Learning Outcome:
 * ---------------
 * Demonstrate how to process large CSV data sets efficiently,
 * generate partial JSON files for scalability testing,
 * and integrate Groovy scripts with MongoDB Atlas for cloud-based storage.
 */

 /**
  * DataPreparation_SplitAndUpload.groovy
  *
  * Reads a large Azure cost CSV (96k records),
  * extracts 40k subset, saves as JSON, and uploads to MongoDB Atlas.
  * Uses external properties file for configuration.
  */
 
 import groovy.json.*
 import com.mongodb.client.*
 import org.bson.Document
 import java.util.Properties
 
//This step is used to check if my properties file is present or not
 def credFile = new File("src/main/resources/mongo.properties")
 def cred = new Properties()
 
 if (!credFile.exists()) {
	 println "mongo.properties file not found!"
	 System.exit(1)
 }
 
 //If present it will excecte the below step i.e., get the property like uri DB and collection name from the file and connect to it 
 credFile.withInputStream { cred.load(it) }
 
 def mongoUri = cred.getProperty("mongo.uri")
 def dbName = cred.getProperty("mongo.db")
 def collectionName = cred.getProperty("mongo.collection")
 
 
 println "   URI: ${mongoUri}"
 println "   Database: ${dbName}"
 println "   Collection: ${collectionName}"
 
//This step will parse my dataset which is in .csv file to .json
 def INPUT_CSV = "src/main/resources/anonymized_costs.csv"
 def OUTPUT_JSON = "src/main/resources/anonymized_costs_small.json"
 
 //Checking if the CSV file exsists in above defined path
 def file = new File(INPUT_CSV)
 if (!file.exists()) {
	 println "CSV file not found!"
	 System.exit(1)
 }
 //If it exsists read it and split to collection for only 40000 records
 println "📥 Reading CSV file..."
 def reader = file.newReader("UTF-8")
 def header = reader.readLine()?.split(",")
 def dataList = []
 
 int lineCount = 0
 reader.eachLine { line ->
	 if (lineCount >= 40000) return
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
 // This step will create new .json file which is already defines as OUTPUT_JSON and upload the datasets here
 println "Loaded ${dataList.size()} records from CSV."
 
 println "Writing subset to JSON file..."
 def jsonContent = JsonOutput.prettyPrint(JsonOutput.toJson(dataList))
 new File(OUTPUT_JSON).write(jsonContent, "UTF-8")
 println "JSON file created: ${OUTPUT_JSON}"
 
//After creating the dataset in .json format and stored in the local system it will try to upload to Mongod atlas
 println "Connecting to MongoDB Atlas..."
 try {
	 def mongoClient = MongoClients.create(mongoUri)
	 def database = mongoClient.getDatabase(dbName)
	 def collection = database.getCollection(collectionName)
 
	 println "Uploading records to '${collectionName}'..."
	 def documents = dataList.collect { new Document(it) }
 
	 collection.insertMany(documents)
	 println "Successfully uploaded ${documents.size()} records to MongoDB Atlas."
 
	 mongoClient.close()
 } catch (Exception e) {
	 println "Error during MongoDB upload: ${e.message}"
 }
 
 println "Task completed successfully!"
 
 
 
 
