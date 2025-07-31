package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.config.AWSConfig;
import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.enums.ErrorCode;
import com.myproject.expense_tracker.utils.WordToNumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyzeReceiptService {

    private static Logger logger = LoggerFactory.getLogger(AnalyzeReceiptService.class);
    @Autowired
    private AWSConfig awsConfig;

    public ExpenseDto analyzeExpenseReceipt(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename().toLowerCase();
        if(fileName.contains(".pdf")
                || fileName.contains(".jpg")
                || fileName.contains(".jpeg")
                ||fileName.contains(".png")
        ){
        logger.info("Analyzing file");
        TextractClient textractClient = awsConfig.textractClient();

        Document document = Document.builder()
                .bytes(SdkBytes.fromByteArray(file.getBytes()))
                .build();

        AnalyzeExpenseRequest request = AnalyzeExpenseRequest.builder()
                .document(document)
                .build();

        AnalyzeExpenseResponse response = textractClient.analyzeExpense(request);

        Map<String, String> extractedData = new HashMap<>();

        for(ExpenseDocument expenseDocument : response.expenseDocuments()){
            for(ExpenseField field : expenseDocument.summaryFields()){
                String type = field.type().text();
                String value = field.valueDetection() != null ? field.valueDetection().text() : "N/A";
                extractedData.put(type, value);
            }
        }
        ExpenseDto expenseDto = new ExpenseDto();
        String totalText = extractedData.get("TOTAL");
        if(totalText.matches(".*\\d.*"))
            totalText = totalText.replaceAll("[^\\d.]", "");
        Double totalAmount;
        try {
            totalAmount = Double.parseDouble(totalText);
        } catch (NumberFormatException e) {
            totalAmount = WordToNumberUtil.convert(totalText); // fallback
        }
        expenseDto.setAmount(totalAmount);

            String dateText = extractedData.get("INVOICE_RECEIPT_DATE");
            expenseDto.setDate(parseDate(dateText));
            logger.info("File analyzed and data fetched:");
            logger.info("Date extracted is: "+expenseDto.getDate());
            logger.info("Amount extracted is:" + expenseDto.getAmount() );

            return expenseDto;
        }else {
            logger.error("Unsupported file format!");
            throw new IllegalArgumentException(ErrorCode.UNSUPPORTED_FILE_FORMAT.getMessage());
        }
    }

    public static LocalDate parseDate(String dateStr) {
        List<String> patterns = List.of(
                "dd-MM-yyyy",
                "dd/MM/yyyy",
                "yyyy-MM-dd",
                "dd-MMM-yyyy",
                "dd MMM yyyy",
                "MMM d, yyyy",
                "dd MMMM yyyy",
                "M/d/yyyy",
                "M-d-yyyy",
                "d-MMM-yyyy",
                "dd.MM.yyyy",
                "MM.dd.yyyy"
        );

        for (String pattern : patterns) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
            } catch (Exception ignored) {}
        }
        logger.warn("Failed to parse date: {}", dateStr);
        return LocalDate.now();
    }


}
