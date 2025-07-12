package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.repository.IncomeRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Double> getMonthlySummary(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonth());
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        double totalIncome = incomeRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double totalExpense = expenseRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double savings = totalIncome - totalExpense;

        return Map.of(
                "totalIncome", totalIncome,
                "totalExpense", totalExpense,
                "savings", savings
        );
    }

    public Map<String, Double> getCategorySummary(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Object[]> results = expenseRepository.sumByCategory(user);

        Map<String, Double> summary = new HashMap<>();
        for (Object[] result : results) {
            summary.put((String) result[0], (Double) result[1]);
        }

        return summary;
    }

    public List<Map<String, Object>> getMonthlyTrend(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Map<String, Object>> trendList = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            double income = incomeRepository.sumByUserAndDateBetween(user, start, end).orElse(0.0);
            double expense = expenseRepository.sumByUserAndDateBetween(user, start, end).orElse(0.0);

            Map<String, Object> record = new HashMap<>();
            record.put("month", ym.toString());
            record.put("income", income);
            record.put("expense", expense);
            trendList.add(record);
        }

        return trendList;
    }
    public Map<String, Double> getLastMonthSummary(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        double totalIncome = incomeRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double totalExpense = expenseRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double savings = totalIncome - totalExpense;

        return Map.of(
                "totalIncome", totalIncome,
                "totalExpense", totalExpense,
                "savings", savings
        );
    }

    public Map<String, Double> getMonthlyCategorySummary(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Object[]> results = expenseRepository.sumByCategory(user);

        Map<String, Double> summary = new HashMap<>();
        for (Object[] result : results) {
            summary.put((String) result[0], (Double) result[1]);
        }

        return summary;
    }

}