package com.example.financialSystem.models.mapper;

import com.example.financialSystem.models.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.models.dto.requests.ExpenseRequest;
import com.example.financialSystem.models.dto.responses.ExpenseResponse;
import com.example.financialSystem.models.entity.Expense;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(target = "isFixed")
    Expense toEntity(ExpenseRequest expenseRequest);

    @Mapping(target = "financialType", constant = "EXPENSE")
    ExpenseResponse toResponse(Expense entity);

    List<ExpenseResponse> toResponseList(List<Expense> entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromPatch(ExpensePatchRequest expensePatchRequest, @MappingTarget Expense entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromUpdate(ExpenseRequest expenseRequest, @MappingTarget Expense entity);
}
