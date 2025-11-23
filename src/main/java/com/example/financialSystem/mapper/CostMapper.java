package com.example.financialSystem.mapper;

import com.example.financialSystem.dto.requests.CostPatchRequest;
import com.example.financialSystem.dto.requests.CostRequest;
import com.example.financialSystem.dto.responses.CostResponse;
import com.example.financialSystem.model.Cost;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CostMapper {
    public Cost toEntity(CostRequest costRequest) {
        Cost cost = new Cost();
        cost.setType(costRequest.costType());
        cost.setObservation(costRequest.observation());
        cost.setValue(costRequest.value());
        cost.setDateFinancial(costRequest.dateFinancial());
        cost.setBaseCurrency(costRequest.baseCurrency());

        return cost;
    }

    public void toPatchEntity(CostPatchRequest patchRequest) {
        Cost cost = new Cost();
        cost.setType(patchRequest.getCostType());
        cost.setObservation(patchRequest.getObservation());
        cost.setValue(patchRequest.getValue());
        cost.setDateFinancial(patchRequest.getDateFinancial());
        cost.setBaseCurrency(patchRequest.getBaseCurrency());

    }

    public List<CostResponse> toDtoList(List<Cost> costs) {
        return costs.stream()
                .map(CostResponse::new)
                .toList();
    }
}
