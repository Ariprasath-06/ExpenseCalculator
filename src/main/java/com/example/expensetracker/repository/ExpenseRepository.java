package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT MONTH(e.date) as month, SUM(e.amount) as total FROM Expense e WHERE YEAR(e.date) = YEAR(CURRENT_DATE) GROUP BY MONTH(e.date) ORDER BY month")
    List<Object[]> getMonthlyExpenses();
}