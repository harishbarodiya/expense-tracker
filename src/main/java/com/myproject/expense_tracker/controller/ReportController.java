package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.ApiResponseDto;
import com.myproject.expense_tracker.enums.ApiStatus;
import com.myproject.expense_tracker.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private static Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @GetMapping("/monthly-summary")
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummary() {
        logger.info("Fetching monthly summary for logged-in user.");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        reportService.getMonthlySummary()
                )
        );
    }

    @GetMapping("/category-summary")
    public ResponseEntity<?> getCategorySummary() {
        logger.info("Fetching category summary for logged-in user.");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        reportService.getCategorySummary()
                )
        );
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<?> getMonthlyTrend() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        reportService.getMonthlyTrend()
                )
        );
    }
}