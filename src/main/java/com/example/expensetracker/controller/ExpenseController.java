package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ExpenseController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    private ExpenseService expenseService;

    @Autowired
    public void setExpenseService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, 
                          @RequestParam(required = false) String filter,
                          @RequestParam(required = false) Integer month,
                          @RequestParam(required = false) Integer year) {
        logger.info("Dashboard accessed with filter: {}, month: {}, year: {}", filter, month, year);
        
        User currentUser = expenseService.getCurrentUser();
        List<Expense> expenses = expenseService.getFilteredExpenses(filter, month, year, currentUser);
        
        model.addAttribute("expenses", expenses);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("currentUser", currentUser.getUsername());
        
        logger.info("Dashboard rendered successfully for user: {}", currentUser.getUsername());
        return "dashboard";
    }
    
    @GetMapping("/add-expense")
    public String showAddForm(Model model) {
        logger.info("Add expense form requested");
        model.addAttribute("expense", new Expense());
        return "add-expense";
    }
    
    @PostMapping("/add")
    public String addExpense(@ModelAttribute Expense expense) {
        logger.info("Processing add expense request");
        expenseService.saveExpense(expense);
        return "redirect:/dashboard";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        logger.info("Processing delete expense request for ID: {}", id);
        expenseService.deleteExpense(id);
        return "redirect:/dashboard";
    }
}