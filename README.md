# 🌩️ Cloud Service Cost Analysis using Groovy and MongoDB

### 🎓 University of Leicester – MSc Cloud Computing  
**Module:** Agile Cloud Automation  
**Team Project – 6 Members**

---

## 👥 Team Members
| Name |
|------|
| **Akshay** |
| **Anirudh** |
| **Shailesh** |
| **Shyan** |
| **Honey** |
|**Rajesh** |

---

## 📘 Project Overview
This project analyzes **Azure Cloud Service usage and cost data** using **Groovy scripts** and **MongoDB Atlas**.  
The goal is to identify how different service categories and regions contribute to overall cloud costs and to evaluate **scalability** between local and cloud-based data analysis.

---

## 🧩 Exercises Summary

| Exercise | Description | Focus |
|-----------|--------------|--------|
| **Exercise 1** | Dataset selection and query planning | Data understanding |
| **Exercise 2** | Analyze data locally with Groovy scripts | Local CSV processing |
| **Exercise 3** | Replicate the same analysis in MongoDB Atlas | Cloud NoSQL analytics |
| **Exercise 4** | Compare both approaches | Scalability & performance |
| **Exercise 5** | Extended scalability test using full dataset | Validation with large data |
| **Extra Task** | Split 96 K CSV → 40 K subset + upload to MongoDB | Data preparation pipeline |

---

## 🧠 Aim
> To analyze Azure cloud cost data using Groovy and MongoDB,  
> showing how services and regions contribute to overall spending,  
> and evaluating scalability between local and cloud environments.

---

## 🧾 Problem Statement
Organizations using Azure often struggle to control costs spread across multiple services and regions.  
This project demonstrates a data-driven approach to monitor, aggregate, and visualize spending trends for cost governance and optimization.

---

## 🎯 Objectives
- Filter, group, and aggregate Azure cost data to detect high-spend services.  
- Compare Groovy-based local processing with MongoDB’s aggregation pipelines.  
- Evaluate scalability and performance differences.  
- Produce insights for cloud cost optimization.

---

## 🔍 Intended Query
> “Find total and average monthly cost per service category and region between  
> **22-Dec-2022 and 22-Jan-2023**, and observe spending evolution.”

---

## ⚙️ Implementation Summary

| Exercise | Environment | Dataset | Description |
|-----------|--------------|----------|--------------|
| **2** | Groovy (local) | 40 K subset | CSV filtering → grouping → aggregation |
| **3** | MongoDB Atlas | 40 K subset | Same query via aggregation pipeline |
| **5** | Scalability Test | 96 K full | Repeat analysis to validate scaling |

---

## 🧩 Extra Task – Data Preparation

### File: `DataPreparation_SplitAndUpload.groovy`
**Purpose:**  
Read the large CSV (~96 K records), extract 40 K records, convert to JSON, store locally, and upload to MongoDB.

**Config File:** `mongodb.properties`

