package com.myproject.expense_tracker.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double amount;
    private String currency;
    private String category;
    private LocalDate date;
    private String receiptKey;

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReceiptKey() {
        return receiptKey;
    }

    public void setReceiptKey(String receiptKey) {
        this.receiptKey = receiptKey;
    }
}

