package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.ApiResponseDto;
import com.myproject.expense_tracker.dto.LoginRequestDto;
import com.myproject.expense_tracker.dto.SignUpRequestDto;
import com.myproject.expense_tracker.enums.ApiStatus;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.service.UserService;
import com.myproject.expense_tracker.utils.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<String>> register(@Valid @RequestBody SignUpRequestDto signUpRequest){
        logger.info("Registering new user.");
        userService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        "User registered successfully"
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<String>> login(@Valid @RequestBody LoginRequestDto loginRequest){
        logger.info("Logging-in a user.");
        User user = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiStatus.SUCCESS,
                        HttpStatus.OK.value(),
                        ApiStatus.SUCCESS.name(),
                        LocalDateTime.now(),
                        token
                )
        );
    }
}
