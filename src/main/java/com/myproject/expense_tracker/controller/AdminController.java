package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.dto.UserDto;
import com.myproject.expense_tracker.dto.UserResponseDto;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.service.ExpenseService;
import com.myproject.expense_tracker.service.IncomeService;
import com.myproject.expense_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private IncomeService incomeService;
    @Autowired private ExpenseService expenseService;

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> usersResponseDtos = userService.getAllUsers();
        return usersResponseDtos;// ResponseEntity.ok(usersDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("User deleted successfully!!");
    }


    @GetMapping("/all-incomes")
    public List<IncomeDto> getIncome() {
        return incomeService.getAllIncomes();
    }

    @GetMapping("/all-expenses")
    public List<ExpenseDto> getExpense() {
        return expenseService.getAllExpenses();
    }

}