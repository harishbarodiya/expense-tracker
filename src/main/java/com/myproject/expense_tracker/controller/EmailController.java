//package com.myproject.expense_tracker.controller;
//
//import com.myproject.expense_tracker.service.EmailService;
//import org.apache.coyote.Response;
//import org.mapstruct.Mapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/email")
//public class EmailController {
//
//    @Autowired
//    private EmailService emailService;
//
//    @GetMapping("/report")
//    public ResponseEntity<String> emailReport(){
//        emailService.sendSimpleEmail(
//                "itisafakemail111@gmail.com",
//                "Test Expense Report",
//                "This is a test report email from AWS SES"
//        );
//        return ResponseEntity.ok("Email sent!");
//    }
//}
