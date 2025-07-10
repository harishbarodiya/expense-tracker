package com.myproject.expense_tracker.repository;

import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUser_UserId(Long userId);

//    Optional<Double> sumByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user AND i.date BETWEEN :start AND :end")
    Optional<Double> sumByUserAndDateBetween(@Param("user") User user,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end);
}
