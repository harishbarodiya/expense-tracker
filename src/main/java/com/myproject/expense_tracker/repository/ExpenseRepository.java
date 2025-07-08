package com.myproject.expense_tracker.repository;

import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUser_UserId(Long userId);
}
