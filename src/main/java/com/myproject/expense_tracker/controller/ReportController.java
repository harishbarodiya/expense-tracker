package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/monthly-summary")
    public ResponseEntity<?> getMonthlySummary() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reportService.getMonthlySummary(username));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<?> getCategorySummary() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reportService.getCategorySummary(username));
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<?> getMonthlyTrend() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reportService.getMonthlyTrend(username));
    }
}