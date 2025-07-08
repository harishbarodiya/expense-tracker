package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/add-expense")
    public ResponseEntity<String> addExpense(@RequestBody ExpenseDto expenseDto){
//        System.out.println(expenseDto.getAmount());
        expenseService.addExpense(expenseDto);
        return ResponseEntity.ok("Expense added successfully!!");
    }

    @GetMapping("/my-expenses")
    public List<ExpenseDto> getMyExpenses() {
        return expenseService.getMyExpenses();
    }
}
