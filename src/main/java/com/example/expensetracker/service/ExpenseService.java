package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ExpenseService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Expense> getFilteredExpenses(String filter, Integer month, Integer year, User user) {
        LocalDate now = LocalDate.now();
        
        if (month != null && year != null) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
            logger.debug("Retrieved {} expenses for {}/{} for user {}", expenses.size(), month, year, user.getUsername());
            return expenses;
        } else if ("weekly".equals(filter)) {
            LocalDate startOfWeek = now.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
            List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startOfWeek, now);
            logger.debug("Retrieved {} weekly expenses for user {}", expenses.size(), user.getUsername());
            return expenses;
        } else if ("monthly".equals(filter)) {
            List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, now.withDayOfMonth(1), now);
            logger.debug("Retrieved {} monthly expenses for user {}", expenses.size(), user.getUsername());
            return expenses;
        } else if ("yearly".equals(filter)) {
            List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, now.withDayOfYear(1), now);
            logger.debug("Retrieved {} yearly expenses for user {}", expenses.size(), user.getUsername());
            return expenses;
        } else {
            List<Expense> expenses = expenseRepository.findByUser(user);
            logger.debug("Retrieved {} total expenses for user {}", expenses.size(), user.getUsername());
            return expenses;
        }
    }
    
    public List<Object[]> getMonthlyExpenseData(String filter, User user) {
        LocalDate now = LocalDate.now();
        LocalDate startDate, endDate;
        
        if ("weekly".equals(filter)) {
            startDate = now.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
            endDate = now;
        } else if ("monthly".equals(filter)) {
            startDate = now.withDayOfMonth(1);
            endDate = now;
        } else if ("yearly".equals(filter)) {
            startDate = now.withDayOfYear(1);
            endDate = now;
        } else {
            startDate = LocalDate.of(now.getYear(), 1, 1);
            endDate = LocalDate.of(now.getYear(), 12, 31);
        }
        
        List<Object[]> monthlyData = expenseRepository.getMonthlyExpensesByUserAndDateRange(user, startDate, endDate);
        logger.debug("Retrieved monthly chart data with {} entries for user {}", monthlyData.size(), user.getUsername());
        return monthlyData;
    }
    
    public User getCurrentUser() {
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal()) &&
                authentication.getPrincipal() instanceof User) {
                return (User) authentication.getPrincipal();
            }
            
            // Return null for unauthenticated users
            return null;
        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());
            return null;
        }
    }
    
    public Expense saveExpense(Expense expense) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        expense.setUser(currentUser);
        logger.info("Saving expense: {} - ₹{} on {} for user {}", expense.getDescription(), expense.getAmount(), expense.getDate(), currentUser.getUsername());
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
    
    public java.math.BigDecimal calculateTotalAmount(List<Expense> expenses) {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}