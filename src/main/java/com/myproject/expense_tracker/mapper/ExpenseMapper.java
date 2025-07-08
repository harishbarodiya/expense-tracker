package com.myproject.expense_tracker.mapper;

import com.myproject.expense_tracker.dto.ExpenseDto;
import com.myproject.expense_tracker.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "fullName", target = "user.fullName")
    Expense toExpense(ExpenseDto expenseDto);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    ExpenseDto toExpenseDto(Expense expense);
}
