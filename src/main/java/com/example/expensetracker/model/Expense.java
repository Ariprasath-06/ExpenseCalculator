package com.example.expensetracker.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}