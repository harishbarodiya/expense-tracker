package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.controller.ReportController;
import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.enums.ErrorCode;
import com.myproject.expense_tracker.mapper.ExpenseMapper;
import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private static Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final UserRepository userRepository;
    private final S3Client s3Client;
    private final S3Service s3Service;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, UserRepository userRepository, S3Client s3Client, S3Service s3Service) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
        this.s3Service = s3Service;
    }

    public void addExpense(ExpenseDto expenseDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        Expense expense = expenseMapper.toExpense(expenseDto);
        expense.setUser(user);
        expenseRepository.save(expense);
        logger.info("Expense added successfully!!");
    }

    //    only admin accessible
    public List<ExpenseDto> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        logger.info("{} expenses fetched successfully!!", expenses.size());
        return expenses.stream()
                .map(expenseMapper::toExpenseDto)
                .collect(Collectors.toList());
    }

    public List<ExpenseDto> getMyExpenses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        List<Expense> expenses = expenseRepository.findAllByUser_UserId(user.getUserId());
        logger.info("Expense for {} fetched successfully!!",username);
        return expenses.stream()
                .map(expenseMapper::toExpenseDto)
                .collect(Collectors.toList());
    }

    public void uploadAndAttachReceipt(Long expenseId, MultipartFile file) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        Expense expense = expenseRepository.findByExpenseIdAndUser_Username(expenseId, userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXPENSE_NOT_FOUND.getMessage()));

        String s3Key = s3Service.uploadReceiptToS3(file);

        expense.setReceiptKey(s3Key);
        expenseRepository.save(expense);
        logger.info("Expense receipt has been uploaded and attached to expense-id {}",expenseId);
    }

    public Map.Entry<String, InputStreamResource> downloadReceipt(Long expenseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        Expense expense = expenseRepository.findByExpenseIdAndUser_Username(expenseId, userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXPENSE_NOT_FOUND.getMessage()));

        String receiptKey = expense.getReceiptKey();
        logger.info("Receipt key fetched from expense");
        InputStreamResource inputStreamResource = s3Service.downloadReceipt(receiptKey);
        logger.info("Receipt fetched from S3");
        return Map.entry(receiptKey, inputStreamResource);
    }


}
