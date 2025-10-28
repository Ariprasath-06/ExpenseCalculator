package com.example.expensetracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpenseTrackerApplication {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseTrackerApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Expense Tracker Application...");
        SpringApplication.run(ExpenseTrackerApplication.class, args);
        logger.info("Expense Tracker Application started successfully");
    }

}
