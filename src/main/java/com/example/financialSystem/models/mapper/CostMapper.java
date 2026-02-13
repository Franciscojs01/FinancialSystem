package com.example.financialSystem.models.mapper;

import com.example.financialSystem.models.dto.requests.CostPatchRequest;
import com.example.financialSystem.models.dto.requests.CostRequest;
import com.example.financialSystem.models.dto.responses.CostResponse;
import com.example.financialSystem.models.entity.Cost;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CostMapper {
    Cost toEntity(CostRequest costRequest);

    @Mapping(target = "financialType", constant = "COST")
    CostResponse toResponse(Cost entity);

    List<CostResponse> toResponseList(List<Cost> entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPatch(CostPatchRequest costPatchRequest, @MappingTarget Cost cost);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateFromUpdate(CostRequest costRequest, @MappingTarget Cost cost);


}

