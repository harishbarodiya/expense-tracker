package com.myproject.expense_tracker.controller;

import com.myproject.expense_tracker.dto.UserDto;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.service.UserService;
import com.myproject.expense_tracker.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody UserDto userDto){
        userService.registerUser(userDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest){
        User user = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }
}
