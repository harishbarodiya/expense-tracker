package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.model.Expense;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    private static Logger logger = LoggerFactory.getLogger(S3Service.class);

    private final S3Client s3Client;
    private final ExpenseRepository expenseRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client, ExpenseRepository expenseRepository) {
        this.s3Client = s3Client;
        this.expenseRepository = expenseRepository;
    }

    public String uploadReceiptToS3(MultipartFile file) throws IOException{

        String key = "expense-receipts/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
       PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        logger.info("File uploaded to S3 bucket!");
        return key;
    }

    public InputStreamResource downloadReceipt(String receiptKey){
        logger.info("Downloading from S3 bucket!");
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(receiptKey)
                        .build());
        return new InputStreamResource(s3Object);
    }
}
