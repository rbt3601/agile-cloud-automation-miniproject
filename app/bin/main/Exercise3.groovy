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
