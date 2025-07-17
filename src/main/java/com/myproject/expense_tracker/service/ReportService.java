package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.enums.ErrorCode;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.repository.IncomeRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Double> getMonthlySummary() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonth());
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        double totalIncome = incomeRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double totalExpense = expenseRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double savings = totalIncome - totalExpense;
        logger.info("{} month summary fetched for {}", currentMonth, userName);
        return Map.of(
                "totalIncome", totalIncome,
                "totalExpense", totalExpense,
                "savings", savings
        );
    }


    public Map<String, Double> getCategorySummary() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        List<Object[]> results = expenseRepository.sumByCategory(user);

        Map<String, Double> summary = new HashMap<>();
        for (Object[] result : results) {
            summary.put((String) result[0], (Double) result[1]);
        }
        logger.info("Category wise summary fetched for {}", userName);
        return summary;
    }

       public List<Map<String, Object>> getMonthlyTrend() {
           Authentication auth = SecurityContextHolder.getContext().getAuthentication();
           String userName = auth.getName();

           User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
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
           logger.info("monthly trend fetched for {}", userName);
        return trendList;
    }

    public Map<String, Double> getLastMonthSummary(String userName){
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        double totalIncome = incomeRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double totalExpense = expenseRepository.sumByUserAndDateBetween(user, startDate, endDate).orElse(0.0);
        double savings = totalIncome - totalExpense;

        logger.info("{} month summary fetched for {}", startDate.getMonth(), userName);
        return Map.of(
                "totalIncome", totalIncome,
                "totalExpense", totalExpense,
                "savings", savings
        );
    }

    public Map<String, Double> getMonthlyCategorySummary(String userName){
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Object[]> results = expenseRepository.sumByCategoryForMonth(user, startDate, endDate);

        Map<String, Double> summary = new HashMap<>();
        for (Object[] result : results) {
            summary.put((String) result[0], (Double) result[1]);
        }
        logger.info("{} month summary fetched for {}", startDate.getMonth(), userName);
        return summary;
    }

}