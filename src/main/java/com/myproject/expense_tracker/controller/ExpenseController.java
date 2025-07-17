package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.service.ExpenseService;
import com.myproject.expense_tracker.service.ReceiptOcrService;
import com.myproject.expense_tracker.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired private ExpenseService expenseService;
    @Autowired private S3Service s3Service;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private ReceiptOcrService receiptOcrService;

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

    @PostMapping("/upload-receipt/{expenseId}")
    public ResponseEntity<String> uploadReceipt(@PathVariable Long expenseId,
                                                @RequestParam("file") MultipartFile file){
        try{
            String s3Key = s3Service.uploadreceipt(file);
            expenseService.attachReceipt(expenseId, s3Key);
            return ResponseEntity.ok(s3Key);
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }


    @GetMapping("/download-receipt/{expenseId}")
    public ResponseEntity<InputStreamResource> downloadReceipt(@PathVariable Long expenseId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        Expense expense = expenseRepository.findByExpenseIdAndUser_Username(expenseId, userName)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        String receiptKey = expense.getReceiptKey();
        InputStreamResource inputStreamResource = s3Service.downloadReceipt(receiptKey);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ receiptKey + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStreamResource));
    }


    @PostMapping("/upload-ocr")
    public ResponseEntity<?> uploadAndExtractExpense(@RequestParam("file") MultipartFile file){
        try{
            return ResponseEntity.ok(receiptOcrService.parseReceiptAndFetchData(file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}

