# ☁️ Cloud Service Cost Analysis using Groovy and MongoDB

**Module:** CO7217 – Agile Cloud Automation  
**Course:** MSc Cloud Computing, University of Leicester  
**Academic Year:** 2025–26  

---

## 👥 Team Members
| Name |
|------|
| Akshay Nagaraj |
| Anirudh Ramaprasad |
| Shailesh Ravindra Dalvi |
| Sayan Suhel Khan |
| Honeykumar Ajitbhai Patel |
| **Rajesh Bennegere Theertheswara** |

---

## 📘 Project Overview
This project analyses **Azure Cloud Cost data** using both **local** and **cloud-based** environments.  
Groovy is used for **local data processing**, while **MongoDB Atlas** is used for **cloud aggregation**.  
The aim is to compare **performance, scalability, and efficiency** across both environments using identical datasets.

---

## 🎯 Aim
To analyse Azure cloud cost data using Groovy and MongoDB,  
showing how services and regions contribute to overall spending and  
evaluating scalability between local and cloud environments.

---

## 🧩 Exercise Summary
| Exercise | Description | Focus |
|-----------|--------------|--------|
| **Exercise 1** | Dataset selection and planning | Understanding and defining data scope |
| **Exercise 2** | Local data analysis using Groovy | Local filtering, grouping, and aggregation |
| **Exercise 3** | Cloud data analysis using MongoDB Atlas | Aggregation pipeline execution |
| **Exercise 4** | Comparison between local and cloud | Performance & scalability evaluation |
| **Exercise 5** | Scalability analysis (30K / 60K / 90K datasets) | Measuring data growth impact |

---

## 🧾 Data Schema (Fields Used)
**Fields:**  
`Date`, `ConsumedService`, `ResourceLocation`, `CostInBillingCurrency`

Each record represents one Azure billing entry containing service, region, and cost information.

---

## 🔍 Query Logic (Common for All Exercises)
1. **Selection** – Select `Date`, `ServiceName`, `Region`, `Cost`  
2. **Filtering** – Records from *22 Dec 2022 to 22 Jan 2023*  
3. **Grouping** – Group by `ServiceName` and `Region`  
4. **Aggregation** – Compute total and average cost per group  
5. **Sorting** – Descending order by total cost  
6. **Limiting** – Display top 10 services by cost  

---

## 💻 Exercise 2 – Local Data Analysis (Groovy)
**Script:** `Exercise2.groovy`

### Description
- Reads anonymized_costs JSON files (30K, 60K, 90K)  
- Filters by date, groups by service & region  
- Computes total and average cost  
- Displays top 10 high-cost services  
- Saves output to `src/main/resources/output/summary_costs.json`  
- Measures execution time using `System.nanoTime()`  

### Insight
Local Groovy analysis works well for small data but slows down significantly as dataset size increases due to in-memory computation.

---

## ☁️ Exercise 3 – Cloud Data Analysis (MongoDB Atlas)
**Script:** `Exercise3.groovy`

### Description
- Connects to MongoDB Atlas using credentials in `mongo.properties`  
- Executes aggregation pipeline with `$addFields`, `$match`, `$group`, `$sort`  
- Aggregates service and regional costs  
- Saves output as `Exercise-3.json`  
- Displays results and total execution time  

### Insight
MongoDB Atlas handles large-scale aggregations faster due to **server-side aggregation**, **parallel query execution**, and **auto-optimized indexing**.

---

## 🧰 Data Preparation – `DataPreparation_SplitUpload.groovy`
### Description
- Reads `anonymized_costs.csv` (~96K records)  
- Converts CSV to JSON format  
- Splits data into:
  - `anonymized_costs_30.json` (30K)  
  - `anonymized_costs_60.json` (60K)  
  - `anonymized_costs_90.json` (90K)  
- Uploads each dataset to MongoDB Atlas collections:
  - `azurecosts_30`  
  - `azurecosts_60`  
  - `azurecosts_90`

---

## ⚡ Exercise 5 – Scalability Test and Analysis
**Scripts:**
- `Exercise5_1_LocalAnalysis.groovy` – Local analysis  
- `Exercise5_2_CloudAnalysis.groovy` – Cloud analysis  

**Datasets:**  
30K / 60K / 90K (local JSON & MongoDB collections)

### Description
- Measures total execution time for each dataset  
- Compares performance between Groovy (local) and MongoDB (cloud)  
- Stores results as JSON summaries  

### Observation
Execution time increases in both environments with dataset size.  
However, **MongoDB Atlas** demonstrates stronger scalability and stability due to **parallel aggregation** and **optimized performance**.

---

## 📊 Output Files
| File | Description |
|-------|--------------|
| `summary_costs.json` | Local Aggregation Result |
| `Exercise-3.json` | Cloud Aggregation Result |
| `local_scalability_summary.json` | Local Scalability Summary |
| `cloud_scalability_summary.json` | Cloud Scalability Summary |
| `upload_summary.json` | Dataset Upload Log |

---

## 🔗 Project Resources
| Resource | Description | Link |
|-----------|--------------|------|
| **GitHub Repository** | Full source code and documentation | [GitHub Link](https://github.com/rbt3601/agile-cloud-automation-miniproject)) |
| **Group Progress Tracker (Excel)** | Task allocation and weekly progress | [Excel Link](https://uniofleicester-my.sharepoint.com/:x:/r/personal/rbt3_student_le_ac_uk/Documents/Agile_Cloud_Automation_Team_Tracker.xlsx?d=w824157009eee486f8e26dcf19d35601c&csf=1&web=1&e=kgXYjt)) |

---

## 🧭 Execution Order
1. `DataPreparation_SplitUpload.groovy`  
2. `Exercise2.groovy`  
3. `Exercise3.groovy`  
4. `Exercise5_1_LocalAnalysis.groovy`  
5. `Exercise5_2_CloudAnalysis.groovy`

---

## ⚙️ Gradle Execution Commands
```bash
gradle run
