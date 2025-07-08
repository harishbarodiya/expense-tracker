package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @PostMapping("/add-income")
    public ResponseEntity<String> addIncome(@RequestBody IncomeDto incomeDto){
//        System.out.println(incomeDto.getAmount());
        incomeService.addIncome(incomeDto);
        return ResponseEntity.ok("Income added successfully!!");
    }


    @GetMapping("/my-incomes")
    public List<IncomeDto> getMyIncomes() {
        return incomeService.getMyIncomes();
    }
}
