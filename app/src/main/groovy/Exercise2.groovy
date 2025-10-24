/**
 * Exercise 2 – Cloud Service Cost Analysis using Groovy
 *
 * Task:
 * -----
 * Using Groovy, perform an analysis of Azure cloud cost data stored in
 * "anonymized_costs.csv" to understand spending patterns across different
 * services and regions.
 *
 * You need to:
 * 1. Read the CSV file from "src/main/resources/anonymized_costs.csv".
 * 2. Parse the Date column correctly.
 * 3. Filter the records to include only data between 22 December 2022
 *    and 22 January 2023.
 * 4. Select the relevant fields: Date, ServiceName, Region, and Cost.
 * 5. Group the filtered data by ServiceName and Region.
 * 6. Calculate the total and average cost for each group.
 * 7. Convert the filtered and grouped data to JSON format.
 * 8. Save the output to:
 *        - anonymized_costs_filtered.json
 *        - summary_costs.json
 * 9. Print a short summary showing the top 5 services by total cost.
 *
 * Objective:
 * ----------
 * Demonstrate how Groovy can perform selection, projection, filtering,
 * grouping, and aggregation operations similar to NoSQL queries.
 *
 * Output:
 * --------
 * - anonymized_costs_filtered.json (filtered dataset)
 * - summary_costs.json (aggregated summary)
 *
 * Learning Outcome:
 * -----------------
 * This exercise helps you understand how Groovy can be used for data
 * manipulation and analysis tasks in cloud cost analytics.
 */
