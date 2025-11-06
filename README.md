# ☁️ Cloud Service Cost Analysis using Groovy and MongoDB

**Module:** CO7217 – Agile Cloud Automation  
**Course:** MSc Cloud Computing, University of Leicester  
**Academic Year:** 2025 – 26  
**Team Members:** rbt3 (Lead), an448, ar719, srd22, ssk53, hap19

## 📘 Project Overview

This mini-project demonstrates how **cloud cost data** can be analysed using both **local processing (Groovy)** and **cloud-based processing (MongoDB Atlas)**.  
The same dataset of Azure service usage and cost records is explored in two environments to compare **performance, scalability, and efficiency**.

---

## 🎯 Aim

To analyse Azure cloud service cost data using Groovy and MongoDB,  
showing how different services and regions contribute to overall spending  
and evaluating **scalability between local and cloud environments**.
---
## 🧩 Exercises Summary

| Exercise | Description | Focus |
|-----------|--------------|--------|
| **Exercise 1** | Dataset selection and query planning | Understanding data and defining analytical scope |
| **Exercise 2** | Local data analysis using Groovy scripts | Filtering, grouping, and aggregation on local JSON dataset |
| **Exercise 3** | Cloud data analysis in MongoDB Atlas | Implementing equivalent aggregation pipeline in cloud |
| **Exercise 4** | Comparative evaluation | Measuring performance and scalability differences |
| **Exercise 5** | Extended scalability test | Validating results with larger datasets (30 K / 60 K / 90 K records) |

---
## ⚙️ Implementation Overview

The project was implemented in two parallel environments to observe how execution time and scalability differ between a **local setup** and a **cloud-based NoSQL database**.

| Environment | Dataset | Description |
|--------------|----------|--------------|
| **Local (Groovy)** | anonymized_costs_30 / 60 / 90 .json | Reads JSON data, filters by date, groups by service & region, calculates total and average cost. |
| **Cloud (MongoDB Atlas)** | azurecosts_30 / 60 / 90 | Performs the same aggregation pipeline server-side using `$addFields`, `$match`, `$group`, and `$sort`. |
| **Comparison** | Execution time vs. dataset size | Used to validate performance improvement and scalability of MongoDB over local computation. |

---
## 🧾 Data Schema

Each record in the dataset represents a single Azure billing entry with detailed service-usage information.

| Field | Type | Description |
|--------|------|-------------|
| `InvoiceSectionName` | String | Internal identifier for the billing invoice section. |
| `Date` | String (`MM/dd/yyyy`) | Billing date for the usage record. |
| `MeterCategory` | String | Primary service category (e.g., *Virtual Machines*, *Storage*, *Load Balancer*). |
| `MeterSubCategory` | String | Sub-classification under the main service (e.g., *Standard*, *Premium*). |
| `MeterName` | String | Specific metered unit or SKU name. |
| `SubscriptionName` | String | Unique subscription identifier. |
| `ResourceGroup` | String | Azure resource group linked to the service. |
| `ConsumedService` | String | Azure internal service identifier (used for filtering and grouping). |
| `ResourceLocation` | String | Geographic region where the resource is deployed. |
| `ResourceName` | String | Unique resource instance name. |
| `CostInBillingCurrency` | Decimal | Cost of the service in billing currency for that date. |

**Fields used in analysis:**  
`Date`, `ConsumedService`, `ResourceLocation`, and `CostInBillingCurrency`.

These fields were selected to calculate **total** and **average** cost per service and region.


---

## 🔍 Query Logic

Both local (Groovy) and cloud (MongoDB) scripts follow the same analytical flow:

1. **Selection :** Choose relevant fields — `Date`, `ServiceName`, `Region`, `Cost`.  
2. **Filtering :** Keep records between **22 Dec 2022 – 22 Jan 2023**.  
3. **Grouping :** Group by `ServiceName` and `Region`.  
4. **Aggregation :** Compute **total** and **average** cost per group.  
5. **Sorting :** Order results by total cost in descending order.  
6. **Limiting :** Display the **top 10 services** with highest spending.
---
## 🧰 Data Preparation — `DataPreparation_SplitUpload.groovy`

Before running scalability tests, the raw Azure cost CSV dataset was processed using a Groovy automation script that:

1. **Reads the master CSV file** (`anonymized_costs.csv`) containing ~96 K records.  
2. **Parses each record** and converts the data into JSON format.  
3. **Splits the JSON dataset** into three subsets:  
   - `anonymized_costs_30.json` → 30 K records  
   - `anonymized_costs_60.json` → 60 K records  
   - `anonymized_costs_90.json` → 90 K records  
4. **Stores all JSON files** in the `src/main/resources/` folder for local analysis.  
5. **Uploads each subset** to MongoDB Atlas collections:  
   - `azurecosts_30`  
   - `azurecosts_60`  
   - `azurecosts_90`  
6. Uses credentials from `mongo.properties` to connect securely to the cloud database.

---

### 🧠 Purpose
To generate consistent datasets for both **local** and **cloud** environments, ensuring the same data volume and structure are used for scalability comparison in Exercise 5.

---

### ⚙️ Output
After execution, the following files and collections are created:

**Local Files (JSON):**


## ⚡  Scalability Testing and Analysis

This exercise compares execution time scalability for three datasets of increasing size:

| Dataset | Record Count | Environment | Script |
|----------|--------------|--------------|---------|
| **30 K** | 30,000 records | Local (Groovy) | `Exercise5_1.groovy` |
| **60 K** | 60,000 records | Local (Groovy) | `Exercise5_1.groovy` |
| **90 K** | 90,000 records | Local (Groovy) | `Exercise5_1.groovy` |
| **30 K** | 30,000 records | MongoDB Atlas (Cloud) | `Exercise5_2.groovy` |
| **60 K** | 60,000 records | MongoDB Atlas (Cloud) | `Exercise5_2.groovy` |
| **90 K** | 90,000 records | MongoDB Atlas (Cloud) | `Exercise5_2.groovy` |

---

### 🧠 Objective
To observe how execution time grows with dataset size in **local** and **cloud** environments and determine which scales more efficiently.

---

### ⚙️ Methodology
- Both scripts use identical filtering, grouping, aggregation, and sorting logic.  
- Each run measures total execution time using `System.nanoTime()`.  
- No external caching or indexing is used (Free Tier M0 cluster).  
- Datasets were processed sequentially: 30 K → 60 K → 90 K.

---


### 🧩 Interpretation
- Execution time increases with dataset size in both environments.  
- **MongoDB Atlas** grows more slowly with size because it performs server-side aggregation and parallel processing.  
- **Local Groovy** runs slower as it must read and compute all records sequentially in memory.

---

### 🧾 Scripts
- `Exercise5_1.groovy` → Reads local JSON datasets (30 K / 60 K / 90 K), filters by date, and records total time.  
- `Exercise5_2.groovy` → Runs identical aggregation pipeline in MongoDB Atlas for collections `azurecosts_30`, `azurecosts_60`, `azurecosts_90`.




## 👥 Team Members
| Name |
|------|
| **Akshay** |
| **Anirudh** |
| **Shailesh** |
| **Shyan** |
| **Honey** |
|**Rajesh** |
<img width="2600" height="5821" alt="image" src="https://github.com/user-attachments/assets/8c76dbbd-4fec-47a1-9c3e-123174dae0c6" />
