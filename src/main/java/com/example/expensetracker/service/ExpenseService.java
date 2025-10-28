package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class ExpenseService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    public List<Expense> getFilteredExpenses(String filter) {
        LocalDate now = LocalDate.now();
        
        if ("weekly".equals(filter)) {
            LocalDate startOfWeek = now.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
            List<Expense> expenses = expenseRepository.findByDateBetween(startOfWeek, now);
            logger.debug("Retrieved {} weekly expenses from {} to {}", expenses.size(), startOfWeek, now);
            return expenses;
        } else if ("monthly".equals(filter)) {
            List<Expense> expenses = expenseRepository.findByDateBetween(now.withDayOfMonth(1), now);
            logger.debug("Retrieved {} monthly expenses for {}", expenses.size(), now.getMonth());
            return expenses;
        } else if ("yearly".equals(filter)) {
            List<Expense> expenses = expenseRepository.findByDateBetween(now.withDayOfYear(1), now);
            logger.debug("Retrieved {} yearly expenses for {}", expenses.size(), now.getYear());
            return expenses;
        } else {
            List<Expense> expenses = expenseRepository.findAll();
            logger.debug("Retrieved {} total expenses", expenses.size());
            return expenses;
        }
    }
    
    public List<Object[]> getMonthlyExpenseData() {
        List<Object[]> monthlyData = expenseRepository.getMonthlyExpenses();
        logger.debug("Retrieved monthly chart data with {} entries", monthlyData.size());
        return monthlyData;
    }
    
    public Expense saveExpense(Expense expense) {
        logger.info("Saving expense: {} - ${} on {}", expense.getDescription(), expense.getAmount(), expense.getDate());
        try {
            Expense savedExpense = expenseRepository.save(expense);
            logger.info("Expense saved successfully with ID: {}", savedExpense.getId());
            return savedExpense;
        } catch (Exception e) {
            logger.error("Error saving expense: {}", e.getMessage());
            throw e;
        }
    }
    
    public void deleteExpense(Long id) {
        logger.info("Deleting expense with ID: {}", id);
        try {
            expenseRepository.deleteById(id);
            logger.info("Expense deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting expense with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
}