package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.enums.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {
    private static Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${from}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

//    public void sendSimpleEmail(String to, String subject, String body){
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(mailFrom);
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//        System.out.println("Email sent!");
//    }

    public void sendHtmlEmailWithChart(String to, String subject, String htmlBody, File chartFile) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            message.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            helper.addInline("categoryChart", new FileSystemResource(chartFile)); // match cid:categoryChart

            mailSender.send(message);
            logger.info("Email sent to {} successfully!", to);
        } catch (MessagingException e) {
            logger.error(ErrorCode.EMAIL_SENDING_FAILED.getMessage());
            logger.error(e.getMessage());
            throw new RuntimeException(ErrorCode.EMAIL_SENDING_FAILED.getMessage());
        }
    }
}
