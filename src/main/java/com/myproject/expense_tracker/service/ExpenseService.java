package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.mapper.ExpenseMapper;
import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {



    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final UserRepository userRepository;
    private  final S3Client s3Client;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, UserRepository userRepository, S3Client s3Client) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
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

    public void attachReceipt(Long expenseId, String s3Key){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        Expense expense = expenseRepository.findByExpenseIdAndUser_Username(expenseId, userName)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setReceiptKey(s3Key);

        expenseRepository.save(expense);
    }

}
