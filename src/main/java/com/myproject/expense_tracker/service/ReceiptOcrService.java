package com.myproject.expense_tracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReceiptOcrService {

    private static Logger logger = LoggerFactory.getLogger(ReceiptOcrService.class);

    private final TextractClient textractClient;

    @Autowired
    public ReceiptOcrService(TextractClient textractClient) {
        this.textractClient = textractClient;
    }

    public String extractTextFromReceipt(MultipartFile file) throws IOException{
        TextractClient textractClient1 = TextractClient.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create("expense-tracker"))
                .build();

        Document document = Document.builder()
                .bytes(SdkBytes.fromByteArray(file.getBytes()))
                .build();

        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

        StringBuilder text = new StringBuilder();
        for(Block block : response.blocks()){
            if(block.blockType().equals(BlockType.LINE)) {
                text.append(block.text()).append("\n");
            }
        }
        logger.info("Raw text extracted from receipt!");
        logger.info(text.toString());
        return text.toString();
    }

    public Map<String, Object> parseReceiptAndFetchData(MultipartFile file) throws IOException {
        String rawText = extractTextFromReceipt(file);

        logger.info("Reading amount from receipt");
//        Pattern amountPattern = Pattern.compile("Total[:\\s₹]*([0-9]+(\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE);
        Pattern amountPattern = Pattern.compile("(?i)(Grand\\s*Total|Total\\s*Amount|Amount\\s*Payable|TOTAL|Total\\s*Price)[^\\d]{0,10}([₹]?[\\d,]+(?:\\.\\d{1,2})?)", Pattern.CASE_INSENSITIVE);

        Matcher m1 = amountPattern.matcher(rawText);
        Double amount = null;
        double maxAmount = 0.0;
        while(m1.find()){
            String amountStr = m1.group(2).replaceAll("[₹,]", "");
            double val = Double.parseDouble(amountStr);

            if(val > maxAmount){
                maxAmount = val;
            }
        }
        if (maxAmount > 0.0) {
            amount = maxAmount;
        }

        if (m1.find()) {
            amount = Double.parseDouble(m1.group(1));
        }
        logger.info("Amount readed:{}", amount);

        logger.info("Reading date from receipt");
//        Pattern datePattern = Pattern.compile("(?i)(Dated|Date)[:\\s]*([0-9]{1,2}[-\\s]?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*[-\\s]?[0-9]{4})", Pattern.CASE_INSENSITIVE);
//        Pattern datePattern = Pattern.compile("(?i)(Dated|Date)[:\\s]*([0-9]{1,2}[./\\-\\s]?(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)?[a-z]*[./\\-\\s]?[0-9]{4})", Pattern.CASE_INSENSITIVE);
        Pattern datePattern = Pattern.compile("(?i)(Dated|Date)[:\\s]*([0-9]{1,2}[./\\-\\s]?[A-Za-z]{3,9}[./\\-\\s]?[0-9]{4}|[0-9]{2}[./\\-][0-9]{2}[./\\-][0-9]{4})");
        Matcher m2 = datePattern.matcher(rawText);
        LocalDate date = null;

        if (m2.find()) {
            String dateStr = m2.group(2).trim(); //
            // Normalize all separators to dashes for easier matching
            dateStr = dateStr.replaceAll("[./\\s]", "-");
//            System.out.println("dateStr is:"+dateStr);

                if (dateStr.matches("[0-9]{2}-[0-9]{2}-[0-9]{4}")) {
                    // Numeric format like 10-10-2021
                    DateTimeFormatter numericFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    date = LocalDate.parse(dateStr, numericFormatter);

                } else if (dateStr.matches("[0-9]{1,2}-[A-Za-z]{3,9}-[0-9]{4}")) {
                    // Textual format like 3-Nov-2021 or 03-November-2021
                    DateTimeFormatter textFormatter = DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.ENGLISH);
                        date = LocalDate.parse(dateStr, textFormatter);

                        // Try long month name if short fails
                        textFormatter = DateTimeFormatter.ofPattern("d-MMMM-yyyy", Locale.ENGLISH);
                        date = LocalDate.parse(dateStr, textFormatter);
                    }
//                    System.out.println("dateStr is:"+dateStr);
                }
        logger.info("Date readed:{}", date);

        Map<String, Object> receiptData = new HashMap<>();

        receiptData.put("date", date);
        receiptData.put("amount", amount);
        return receiptData;
    }
}