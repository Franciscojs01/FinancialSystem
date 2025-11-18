package com.example.financialSystem.mapper;

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

    public List<CostResponse> toDtoList(List<Cost> costs) {
        return costs.stream()
                .map(CostResponse::new)
                .toList();
    }
}
