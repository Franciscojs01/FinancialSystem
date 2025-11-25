package com.example.financialSystem.model.mapper;

import com.example.financialSystem.model.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.model.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.dto.responses.InvestmentResponse;
import com.example.financialSystem.model.entity.Investment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvestmentMapper {
    Investment toEntity(InvestmentRequest investmentRequest);

    InvestmentResponse toResponse(Investment entity);

    List<InvestmentResponse> toResponseList(List<Investment> entity);

    InvestmentRequest toRequest(Investment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromPatch(InvestmentPatchRequest investmentPatchRequest, @MappingTarget Investment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromUpdate(InvestmentRequest investmentRequest, @MappingTarget Investment entity);

}






