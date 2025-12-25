package com.example.financialSystem.model.mapper;

import com.example.financialSystem.model.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.model.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.dto.responses.InvestmentResponse;
import com.example.financialSystem.model.entity.Investment;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring", imports = {ChronoUnit.class, LocalDate.class})
public interface InvestmentMapper {

    @Mapping(target = "daysInvested",
            expression = "java((int) ChronoUnit.DAYS.between(entity.getDateFinancial(), LocalDate.now()))")
    @Mapping(target = "financialType", constant = "INVESTMENT")
    InvestmentResponse toResponse(Investment entity);

    List<InvestmentResponse> toResponseList(List<Investment> entity);

    Investment toEntity(InvestmentRequest investmentRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromPatch(InvestmentPatchRequest request, @MappingTarget Investment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromUpdate(InvestmentRequest request, @MappingTarget Investment entity);

}
