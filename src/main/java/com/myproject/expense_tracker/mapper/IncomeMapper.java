package com.myproject.expense_tracker.mapper;

import ch.qos.logback.core.model.ComponentModel;
import com.myproject.expense_tracker.dto.IncomeDto;
import com.myproject.expense_tracker.model.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncomeMapper {

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "fullName", target = "user.fullName")
    Income toIncome(IncomeDto incomeDto);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    IncomeDto toIncomeDto(Income income);
}
