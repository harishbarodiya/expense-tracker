package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.*;
import com.myproject.expense_tracker.enums.ApiStatus;
import com.myproject.expense_tracker.service.ExpenseService;
import com.myproject.expense_tracker.service.IncomeService;
import com.myproject.expense_tracker.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        logger.info("Fetching all users list");
        List<UserResponseDto> usersResponseDtos = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        usersResponseDtos
                )
        );
    }

    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<ApiResponseDto<String>> deleteUser(@PathVariable String username) {
        logger.info("Deleting user {}", username);
        userService.deleteUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        "User deleted successfully!!"
                )
        );
    }


    @GetMapping("/all-incomes")
    public ResponseEntity<ApiResponseDto<List<IncomeDto>>> getIncome() {
        logger.info("Fetching all available incomes.");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        incomeService.getAllIncomes()
                )
        );
    }

    @GetMapping("/all-expenses")
    public ResponseEntity<ApiResponseDto<List<ExpenseDto>>> getExpense() {
        logger.info("Fetching all available expenses.");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        expenseService.getAllExpenses()
                )
        );
    }
}