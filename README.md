====================================================================
☁️ CLOUD SERVICE COST ANALYSIS USING GROOVY AND MONGODB
====================================================================
Module: CO7217 – Agile Cloud Automation
Course: MSc Cloud Computing, University of Leicester
Academic Year: 2025–26
====================================================================

TEAM MEMBERS
-------------
1. Akshay Nagaraj
2. Anirudh Ramaprasad
3. Shailesh Ravindra Dalvi
4. Sayan Suhel Khan
5. Honeykumar Ajitbhai Patel
6. Rajesh Bennegere Theertheswara

====================================================================
PROJECT OVERVIEW
====================================================================
This project analyses Azure Cloud Cost data using both local and 
cloud-based environments. Groovy is used for local data processing, 
while MongoDB Atlas is used for cloud aggregation. 
The aim is to compare performance, scalability, and efficiency 
across both environments using identical datasets.

====================================================================
AIM
====================================================================
To analyse Azure cloud cost data using Groovy and MongoDB, showing 
how services and regions contribute to overall spending and 
evaluating scalability between local and cloud environments.

====================================================================
EXERCISE SUMMARY
====================================================================
Exercise 1 - Dataset selection and planning
Exercise 2 - Local data analysis using Groovy
Exercise 3 - Cloud data analysis using MongoDB Atlas
Exercise 4 - Comparison between local and cloud performance
Exercise 5 - Scalability analysis (30K / 60K / 90K datasets)

====================================================================
DATA SCHEMA (FIELDS USED)
====================================================================
Date, ConsumedService, ResourceLocation, CostInBillingCurrency

Each record represents one Azure billing entry containing service, 
region, and cost information.

====================================================================
QUERY LOGIC (COMMON FOR ALL EXERCISES)
====================================================================
1. Selection  - Select Date, ServiceName, Region, Cost
2. Filtering  - Records from 22 Dec 2022 to 22 Jan 2023
3. Grouping   - Group by ServiceName and Region
4. Aggregation- Compute total and average cost per group
5. Sorting    - Descending order by total cost
6. Limiting   - Display top 10 services by cost

====================================================================
EXERCISE 2 – LOCAL DATA ANALYSIS (GROOVY)
====================================================================
Script: Exercise2.groovy
Description:
- Reads anonymized_costs.json files (30K, 60K, 90K)
- Filters by date, groups by service & region
- Computes total and average cost
- Displays top 10 high-cost services
- Saves JSON output to /resources/output/summary_costs.json
- Measures execution time using System.nanoTime()

Insight:
Local Groovy analysis works well for small data but slows down 
significantly for large datasets due to in-memory processing.

====================================================================
EXERCISE 3 – CLOUD DATA ANALYSIS (MONGODB ATLAS)
====================================================================
Script: Exercise3.groovy
Description:
- Connects to MongoDB Atlas using mongo.properties credentials
- Executes aggregation pipeline:
  $addFields, $match, $group, $sort
- Aggregates service and regional costs
- Saves output to /resources/output/Exercise-3.json
- Prints execution time and summary table

Insight:
MongoDB Atlas performs faster and scales efficiently due to 
server-side aggregation and distributed query execution.

====================================================================
DATA PREPARATION SCRIPT
====================================================================
Script: DataPreparation_SplitUpload.groovy
Description:
- Reads anonymized_costs.csv (~96K records)
- Converts CSV to JSON format
- Splits data into three JSON files:
  anonymized_costs_30.json (30K)
  anonymized_costs_60.json (60K)
  anonymized_costs_90.json (90K)
- Uploads each dataset to MongoDB Atlas:
  azurecosts_30, azurecosts_60, azurecosts_90

====================================================================
EXERCISE 5 – SCALABILITY TEST AND ANALYSIS
====================================================================
Scripts:
- Exercise5_1_LocalAnalysis.groovy  (Local Groovy test)
- Exercise5_2_CloudAnalysis.groovy  (Cloud MongoDB test)

Datasets:
30K / 60K / 90K JSON and Atlas collections

Description:
- Measures total execution time for each dataset
- Compares Groovy (local) vs MongoDB (cloud)
- Stores execution summary in JSON output files

Observation:
Execution time increases in both environments with dataset size,
but MongoDB Atlas shows better scalability and stability due to 
parallel processing and optimized aggregation.

====================================================================
OUTPUT FILES
====================================================================
summary_costs.json                 - Local Aggregation Result
Exercise-3.json                    - Cloud Aggregation Result
local_scalability_summary.json     - Local Scalability Results
cloud_scalability_summary.json     - Cloud Scalability Results
upload_summary.json                - Data Upload Summary

====================================================================
PROJECT RESOURCES
====================================================================
GitHub Repository:
https://github.com/YourUsername/agile-cloud-automation-miniproject

Group Progress Tracker (Excel):
https://your-excel-link-here

====================================================================
EXECUTION ORDER
====================================================================
1. DataPreparation_SplitUpload.groovy
2. Exercise2.groovy
3. Exercise3.groovy
4. Exercise5_1_LocalAnalysis.groovy
5. Exercise5_2_CloudAnalysis.groovy

====================================================================
GRADLE EXECUTION COMMANDS
====================================================================
# Build project
gradlew clean build

# Run a specific exercise
gradlew run --args="src/main/groovy/Exercise2.groovy"

(Or run directly using Groovy)
groovy src/main/groovy/Exercise2.groovy

====================================================================
END OF FILE
====================================================================
