/**
 * Exercise 2 – Cloud Service Cost Analysis using Groovy
 *
 * Task:
 * -----
 * Using Groovy, perform an analysis of Azure cloud service cost data
 * to identify how different service categories and regions contribute
 * to total cloud spending patterns for the period between
 * 22 December 2022 and 22 January 2023.
 *
 * You need to:
 * 1. Read the Azure cost dataset from "src/main/resources/anonymized_costs.csv".
 * 2. Parse the "Date" field correctly.
 * 3. Filter the records to include only data between 22-Dec-2022 and 22-Jan-2023.
 * 4. Perform the following operations using Groovy collections:
 *       - Selection: Retrieve the required fields (Date, ServiceName, Region, Cost)
 *       - Projection: Keep only these selected fields
 *       - Filtering: Apply the specified date range
 *       - Grouping: Group data by ServiceName and Region
 *       - Aggregation: Calculate total and average cost per group
 * 5. Sort the output by total cost (descending order).
 * 6. Print a summary showing the top cost-contributing services and regions.
 * 7. Convert the filtered and aggregated results into JSON format.
 * 8. Save the output files to:
 *       - anonymized_costs_filtered.json (filtered dataset)
 *       - summary_costs.json (aggregated results)
 *
 * Objective:
 * ----------
 * To perform NoSQL-style operations (selection, projection, filtering,
 * grouping, aggregation) using Groovy collections on Azure cloud cost data.
 * This exercise demonstrates how local data processing can reveal key
 * spending patterns without relying on external databases.
 *
 * Output:
 * --------
 * - anonymized_costs_filtered.json
 * - summary_costs.json
 * - Console summary of top services by total cost
 *
 * Learning Outcome:
 * -----------------
 * Demonstrate proficiency in data manipulation using Groovy collections.
 * Understand how Groovy can simulate NoSQL query operations for
 * analyzing cloud cost data locally.
 */
