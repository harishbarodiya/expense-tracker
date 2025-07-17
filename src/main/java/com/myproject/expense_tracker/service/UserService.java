package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.dto.UserDto;
import com.myproject.expense_tracker.dto.UserResponseDto;
import com.myproject.expense_tracker.enums.ErrorCode;
import com.myproject.expense_tracker.enums.Role;
import com.myproject.expense_tracker.mapper.UserMapper;
import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.IncomeRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
//import org.springframework.security.core.userdetails.User;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;

    public void registerUser(UserDto userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        logger.info("User registered in database successfully!");
    }

    public User authenticateUser(String username, String rawPassword){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new BadCredentialsException(ErrorCode.UNAUTHORIZED_ACCESS.getMessage());
        }
        logger.info("Authentication success!");
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        logger.info("User loaded {}!", userName);
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

//  admin only
    public List<UserResponseDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        logger.info("{} users fetched from database successfully!", users.size());
        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(String userName){
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        userRepository.delete(user);
        logger.info("User {} deleted successfully!",userName);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
}
