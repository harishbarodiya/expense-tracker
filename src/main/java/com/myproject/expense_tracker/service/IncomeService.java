package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.enums.ErrorCode;
import com.myproject.expense_tracker.mapper.IncomeMapper;
import com.myproject.expense_tracker.model.Income;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.IncomeRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    private static Logger logger = LoggerFactory.getLogger(IncomeService.class);

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
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        Income income = incomeMapper.toIncome(incomeDto);
        income.setUser(user);
        incomeRepository.save(income);
        logger.info("Income added successfully!");

    }

//    only admin accessible
    public List<IncomeDto> getAllIncomes(){
        List<Income> incomes = incomeRepository.findAll();
        logger.info("{} incomes fetched successfully!",incomes.size());
        return incomes.stream()
                .map(incomeMapper::toIncomeDto)
                .collect(Collectors.toList());
    }

    public List<IncomeDto> getMyIncomes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        List<Income> incomes = incomeRepository.findAllByUser_UserId(user.getUserId());
        logger.info("Incomes for {} fetched successfully!!",username);
        return incomes.stream()
                .map(incomeMapper::toIncomeDto)
                .collect(Collectors.toList());
    }
}
