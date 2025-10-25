/**
 * Exercise 5 – In-Depth Scalability Analysis
 *
 * Task:
 * -----
 * Extend the scalability analysis from Exercise 4 by performing additional
 * experiments and research to validate how both Groovy and MongoDB behave
 * when the dataset grows. The goal is to demonstrate, with evidence, which
 * approach scales better as data size and complexity increase.
 *
 * You need to:
 * 1. Use the same Azure cloud cost dataset from Exercises 2 and 3, and
 *    optionally include a second dataset (for example, February 2023) to
 *    simulate data growth.
 *
 * 2. Combine or extend the dataset and re-run the same analytical query:
 *       - Selection, Projection, Filtering, Grouping, and Aggregation
 *       - Time range: 22-Dec-2022 to 22-Jan-2023, then extended to Feb-2023
 *
 * 3. Measure and record how query performance changes in each environment:
 *       - Execution time for Groovy (local)
 *       - Execution time for MongoDB (cloud)
 *       - Memory or CPU usage observations (optional)
 *
 * 4. Create a simple comparison table or chart showing how both systems
 *    handle increasing data volumes.
 *
 * 5. Discuss your observations:
 *       - Does MongoDB maintain stable performance as records increase?
 *       - Does Groovy slow down or consume more resources?
 *       - How does the cloud architecture enable scalability?
 *
 * 6. Support your findings with short research references or documentation
 *    from MongoDB or cloud analytics sources to strengthen your discussion.
 *
 * 7. Conclude with a summary of evidence:
 *       - Which approach is more scalable and why
 *       - Practical recommendations for handling large-scale cost data
 *
 * Objective:
 * ----------
 * To validate the scalability findings from Exercise 4 through additional
 * experimentation, demonstrating how MongoDB’s distributed architecture and
 * aggregation pipelines outperform local Groovy processing as data volume
 * grows.
 *
 * Output:
 * --------
 * - Table or chart comparing query performance (Groovy vs MongoDB)
 * - Short analytical summary or slide section describing the results
 *
 * Learning Outcome:
 * -----------------
 * Show deep understanding of scalability by providing quantitative or
 * research-backed evidence. Demonstrate how MongoDB supports large-scale,
 * real-world analytics workloads more efficiently than local processing.
 */
