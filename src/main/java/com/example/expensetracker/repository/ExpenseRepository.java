package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    List<Expense> findByUser(User user);
    
    @Query("SELECT EXTRACT(MONTH FROM e.date) as month, SUM(e.amount) as total FROM Expense e WHERE e.user = :user AND e.date BETWEEN :startDate AND :endDate GROUP BY EXTRACT(MONTH FROM e.date) ORDER BY EXTRACT(MONTH FROM e.date)")
    List<Object[]> getMonthlyExpensesByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}