/**
 * Exercise 3 – Cloud Service Cost Analysis using MongoDB
 *
 * Task:
 * -----
 * Implement the same analytical query from Exercise 2, but using MongoDB Atlas
 * as the data source instead of a local CSV file. The goal is to perform the
 * same set of operations directly within a cloud-based database environment.
 *
 * You need to:
 * 1. Connect to the MongoDB Atlas cluster using your connection string.
 *      Example:
 *      mongodb+srv://<username>:<password>@<cluster-url>/cloudcosts
 *
 * 2. Access the database "cloudcosts" and collection "azure_cost_data".
 *    The collection contains Azure cost records for multiple services and regions.
 *
 * 3. Using MongoDB aggregation pipelines, perform the same operations done in Exercise 2:
 *       - Selection: Retrieve Date, ServiceName, Region, and Cost fields.
 *       - Filtering: Include only records between 22-Dec-2022 and 22-Jan-2023.
 *       - Grouping: Group data by ServiceName and Region.
 *       - Aggregation: Compute total and average cost per group.
 *       - Sorting: Sort results by total cost (descending order).
 *
 * 4. Display the results in JSON or tabular format on the console.
 * 5. Optionally, export the results to a JSON file for future use.
 * 6. Interpret the results to highlight which services and regions had
 *    the highest spending within the selected period.
 *
 * Objective:
 * ----------
 * To demonstrate how MongoDB can execute selection, projection, filtering,
 * grouping, and aggregation operations on cloud-hosted data efficiently,
 * replicating the local analysis done in Groovy (Exercise 2) within a
 * scalable, cloud-based environment.
 *
 * Output:
 * --------
 * - Console output of total and average cost per service and region
 * - Optional: JSON export of aggregated data
 *
 * Learning Outcome:
 * -----------------
 * Gain experience using MongoDB aggregation pipelines to analyze large
 * cloud cost datasets. Understand how a NoSQL database like MongoDB can
 * enhance scalability and performance compared to local processing.
 */

 import com.mongodb.client.*
 import org.bson.Document
 import java.text.SimpleDateFormat
 
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
 
 
 // Printing the database object name
 println "database: ${db.getName()}"
 
 // Printing the database object collection
 db.listCollectionNames().each { println it }
 
 /* Setting date logic for filtering the dataset
  Created the Java object through which we are setting the format of the date and once we pass it as
  a String it can be converted into date object using this class.*/
 def sdf   = new SimpleDateFormat("MM/dd/yyyy")
 
 // Defining start and end date inside both the steps
 def start = sdf.parse("12/22/2022")
 def end   = sdf.parse("01/22/2023")     
 
/* Here we are using $addFields which is a stage in mongoDB aggregation to add a new field and "parsedDate" 
   by converting the existing "Date" String into an actual Date object using mongoDB's $DatefromString*/
 def addDate = new Document("\$addFields",
   new Document("parsedDate",
	 new Document("\$dateFromString",
	   new Document("dateString", "\$Date").append("format","%m/%d/%Y")
	 )
   )
 )
 /*Here we are filtering the documents where "parsedDate" is between start and end date */
 def date_filteration = new Document("\$match",
   new Document("parsedDate", new Document("\$gte", start).append("\$lte", end)) 
 )
 
 /*Inside this part we are projecting specific fields from the dataSet */
 println "\n=== PART 1: Selection (Date, ServiceName, Region, Cost) — first 10 ==="
 def pipeLine_selection = [
	 // Here we are using $project to pick /select and rename fields inside the dataSet
   new Document("\$project",
	 new Document("_id", 0) //Here we are hiding the "_id" fields we are by default inside the mongoDB 
	 
	 
	 
	 //Here we are selecting the date,serviceNAme,Cost and Region additionally renaming several fields
	   .append("Date", 1)
	   .append("ServiceName", "\$MeterCategory")
	   .append("Region", "\$ResourceLocation")
	   .append("Cost",
		 new Document("\$toDouble", // Converting to number
		   new Document("\$ifNull", ["\$CostInBillingCurrency", "0"]) //If the "Cost" field would be null then make it 0
		 )
	   )
   ),
   new Document("\$limit", 10) //Here we are limiting only 10 record from the dataSet
 ]
 col.aggregate(pipeLine_selection).forEach { println it } //Running the aggregation pipeLine_selection and using forloop to print.
 
 //
 println "\n=== PART 2: Filtering (22-Dec-2022 to 22-Jan-2023 inclusive) + Selection — first 10 sorted by date ==="
 def pipeLine_filtering = [
	 //Here we are adding "parsedDate" and filtering dataSet by Date range and sorting by Date
   addDate, //It is adding "parsedDate" field from "Date" String
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
	   .append("parsedDate", 1) //We are keeping this "parsedDate" for sorting 
   ),
   new Document("\$sort", new Document("parsedDate", 1)), //Sorting in ascending with the help of "parsedDate" field
   new Document("\$limit", 10)
 ]
 col.aggregate(pipeLine_filtering).forEach { println it } // //Running the aggregation pipeLine_filtering and using forloop to print.