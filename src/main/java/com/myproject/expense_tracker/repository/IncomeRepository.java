package com.myproject.expense_tracker.repository;

import com.myproject.expense_tracker.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUser_UserId(Long userId);
}
