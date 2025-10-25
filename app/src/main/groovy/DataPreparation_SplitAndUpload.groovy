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
