package com.myproject.expense_tracker.service;

import com.myproject.expense_tracker.enums.ErrorCode;
import com.myproject.expense_tracker.model.User;
import com.myproject.expense_tracker.repository.ExpenseRepository;
import com.myproject.expense_tracker.repository.IncomeRepository;
import com.myproject.expense_tracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @InjectMocks
    private ReportService reportService;


    @BeforeEach
    void setupSecurityContext(){

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testGetMonthlySummary_returnsCorrectSummary(){
        // Given
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonth());
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        when(incomeRepository.sumByUserAndDateBetween(mockUser, startDate, endDate)).thenReturn(Optional.of(1000.0));
        when(expenseRepository.sumByUserAndDateBetween(mockUser,startDate, endDate)).thenReturn(Optional.of(400.0));

        //When
        Map<String, Double> summary = reportService.getMonthlySummary();

        //Then
        assertEquals(1000.0, summary.get("totalIncome"));
        assertEquals(400.0 , summary.get("totalExpense"));
        assertEquals(600.0, summary.get("savings"));


    }

    @Test
    void testGetMonthlySummary_userNotFound_throwsException(){
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, ()->{
            reportService.getMonthlySummary();
        });

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void testGetCategorySummary_returnsCorrectSummary() {

        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        List<Object[]> results = new ArrayList<>();

        Object[] object1 = {new String("Food"), Double.valueOf (100.0)};
        Object[] object2 = {new String("Entertainment"), Double.valueOf (400.0)};
        results.add(object1);
        results.add(object2);

        when(expenseRepository.sumByCategory(mockUser)).thenReturn(results);

//        When
        Map<String, Double> summary = reportService.getCategorySummary();

//        Then
        assertEquals(2, summary.size());
        assertEquals(100.0, summary.get("Food"));
        assertEquals(400.0, summary.get("Entertainment"));
    }

    @Test
    void testGetMonthlyTrend_returnsCorrectResults() {

        User mockUser = new User();
        mockUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        for (int i = 6; i >= 1; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            when(incomeRepository.sumByUserAndDateBetween(mockUser, start, end)).thenReturn(Optional.of(400000.0));
            when(expenseRepository.sumByUserAndDateBetween(mockUser, start, end)).thenReturn(Optional.of(350000.0));
        }

//        when
        List<Map<String, Object>> trends = reportService.getMonthlyTrend();

//        then
        assertEquals(6, trends.size());
        assertEquals("2025-06",trends.get(5).get("month"));
        assertEquals(400000.0,trends.get(0).get("income"));
        assertEquals(350000.0,trends.get(0).get("expense"));
        assertEquals("2025-04",trends.get(3).get("month"));

    }
}
