package com.myproject.expense_tracker.repository;

import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUser_UserId(Long userId);
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.date BETWEEN :start AND :end")
    Optional<Double> sumByUserAndDateBetween(@Param("user") User user,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);


    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user GROUP BY e.category")
    List<Object[]> sumByCategory(@Param("user") User user);

    Optional<Expense> findByExpenseIdAndUser_Username(Long expenseId, String userName);

}
