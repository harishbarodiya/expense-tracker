package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.ApiResponseDto;
import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.enums.ApiStatus;
import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.service.IncomeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    private static Logger logger = LoggerFactory.getLogger(IncomeController.class);
    @Autowired
    private IncomeService incomeService;

    @PostMapping("/add-income")
    public ResponseEntity<ApiResponseDto<String>> addIncome(@Valid @RequestBody IncomeDto incomeDto){
        logger.info("Adding a new income.");
        incomeService.addIncome(incomeDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        "Income added successfully!!"
                )
        );
    }

    @GetMapping("/my-incomes")
    public ResponseEntity<ApiResponseDto<List<IncomeDto>>> getMyIncomes() {
        logger.info("Fetching incomes for logged-in user.");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        incomeService.getMyIncomes()
                )
        );
    }
}
