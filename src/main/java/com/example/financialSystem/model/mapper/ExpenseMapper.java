package com.example.financialSystem.model.mapper;

import com.example.financialSystem.model.dto.requests.ExpensePatchRequest;
import com.example.financialSystem.model.dto.requests.ExpenseRequest;
import com.example.financialSystem.model.dto.responses.ExpenseResponse;
import com.example.financialSystem.model.entity.Expense;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    Expense toEntity(ExpenseRequest expenseRequest);

    ExpenseResponse toResponse(Expense entity);

    List<ExpenseResponse> toResponseList(List<Expense> entity);

    ExpenseRequest toRequest(Expense entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromPatch(ExpensePatchRequest expensePatchRequest, @MappingTarget Expense entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromUpdate(ExpenseRequest expenseRequest, @MappingTarget Expense entity);
}
