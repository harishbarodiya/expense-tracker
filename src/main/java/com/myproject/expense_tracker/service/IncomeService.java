package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.mapper.IncomeMapper;
import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.IncomeRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {



    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final UserRepository userRepository;

    public IncomeService(IncomeRepository incomeRepository, IncomeMapper incomeMapper, UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.incomeMapper = incomeMapper;
        this.userRepository = userRepository;
    }

    public void addIncome(IncomeDto incomeDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Income income = incomeMapper.toIncome(incomeDto);
        income.setUser(user);
        incomeRepository.save(income);
    }

//    only admin accessible
    public List<IncomeDto> getAllIncomes(){
        List<Income> incomes = incomeRepository.findAll();
        return incomes.stream()
                .map(incomeMapper::toIncomeDto)
                .collect(Collectors.toList());
    }

    public List<IncomeDto> getMyIncomes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Income> incomes = incomeRepository.findAllByUser_UserId(user.getUserId());

        return incomes.stream()
                .map(incomeMapper::toIncomeDto)
                .collect(Collectors.toList());
    }

}
