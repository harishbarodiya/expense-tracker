package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.ApiResponseDto;
import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.enums.ApiStatus;
import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.service.ExpenseService;
import com.myproject.expense_tracker.service.ReceiptOcrService;
import com.myproject.expense_tracker.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    private static Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    @Autowired private ExpenseService expenseService;
    @Autowired private S3Service s3Service;
    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private ReceiptOcrService receiptOcrService;

    @PostMapping("/add-expense")
    public ResponseEntity<ApiResponseDto<String>> addExpense(@Valid @RequestBody ExpenseDto expenseDto){
        logger.info("Adding a new expense.");
        expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        "Expense added successfully!!"
                )
        );
    }

    @GetMapping("/my-expenses")
    public ResponseEntity<ApiResponseDto<List<ExpenseDto>>> getMyExpenses() {
        logger.info("Fetching expenses for logged-in user");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        expenseService.getMyExpenses()
                )
        );
    }
    @GetMapping("/download-receipt/{expenseId}")
    public ResponseEntity<InputStreamResource> downloadReceipt(@PathVariable Long expenseId) {
       logger.info("Downloading receipt.");
        Map.Entry<String, InputStreamResource> entry = expenseService.downloadReceipt(expenseId);
        String receiptKey = entry.getKey();
        InputStreamResource inputStreamResource = entry.getValue();
        logger.info("Receipt downloaded successfully!");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ receiptKey + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStreamResource)
                );
    }

    @PostMapping(value = "/upload-ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200", description = "File uploaded successfully")
    public ResponseEntity<ApiResponseDto<?>> uploadAndExtractExpense(@RequestParam("file") MultipartFile file){
        logger.info("Uploading receipt for OCR.");
        try{
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(ApiStatus.SUCCESS,
                            HttpStatus.OK.value(),
                            ApiStatus.SUCCESS.name(),
                            LocalDateTime.now(),
                            receiptOcrService.parseReceiptAndFetchData(file)
                    )
            );
        } catch (Exception e) {
            logger.error("Exception caugth: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponseDto<>(ApiStatus.FAILED,
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ApiStatus.FAILED.name(),
                            LocalDateTime.now(),
                            "Error: " + e.getMessage()
                    )
            );
        }
    }

    @PostMapping(value = "/upload-receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "200", description = "File uploaded successfully")
    public ResponseEntity<ApiResponseDto<String>> uploadReceipt(@RequestParam("expenseId") Long expenseId,
                                                                @RequestParam("file") MultipartFile file) {
        try{
            expenseService.uploadAndAttachReceipt(expenseId, file);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(ApiStatus.SUCCESS,
                            HttpStatus.OK.value(),
                            ApiStatus.SUCCESS.name(),
                            LocalDateTime.now(),
                            "Upload success!"
                    )
            );
        } catch (IOException e){
            logger.error("File upload get failed due to following error:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponseDto<>(ApiStatus.FAILED,
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ApiStatus.FAILED.name(),
                            LocalDateTime.now(),
                            "Upload failed!"
                    )
            );
        }
    }
}

