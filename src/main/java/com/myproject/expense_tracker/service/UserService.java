package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.dto.UserDto;
import com.myproject.expense_tracker.dto.UserResponseDto;
import com.myproject.expense_tracker.enums.Role;
import com.myproject.expense_tracker.mapper.UserMapper;
import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.IncomeRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    }

    public User authenticateUser(String username, String rawPassword){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

//  admin only
    public List<UserResponseDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        System.out.println(">>>>>>>>>>>>>>>>>"+users.size());
        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(String userName){
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(user);
    }
}
