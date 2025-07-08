package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.mapper.ExpenseMapper;
import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {



    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.userRepository = userRepository;
    }

    public void addExpense(ExpenseDto expenseDto){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = expenseMapper.toExpense(expenseDto);
        expense.setUser(user);
        expenseRepository.save(expense);
    }

//    only admin accessible
    public List<ExpenseDto> getAllExpenses(){
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(expenseMapper::toExpenseDto)
                .collect(Collectors.toList());
    }

    public List<ExpenseDto> getMyExpenses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findAllByUser_UserId(user.getUserId());

        return expenses.stream()
                .map(expenseMapper::toExpenseDto)
                .collect(Collectors.toList());
    }

}
