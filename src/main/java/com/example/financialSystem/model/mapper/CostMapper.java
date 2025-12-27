package com.example.financialSystem.model.mapper;

import com.example.financialSystem.model.dto.requests.CostPatchRequest;
import com.example.financialSystem.model.dto.requests.CostRequest;
import com.example.financialSystem.model.dto.responses.CostResponse;
import com.example.financialSystem.model.entity.Cost;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CostMapper {
    Cost toEntity(CostRequest costRequest);

    CostResponse toResponse(Cost entity);

    List<CostResponse> toResponseList(List<Cost> entity);

    CostRequest toRequest(Cost entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPatch(CostPatchRequest costPatchRequest, @MappingTarget Cost cost);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateFromUpdate(CostRequest costRequest, @MappingTarget Cost cost);


}

