package com.example.financialSystem.mapper;

import com.example.financialSystem.dto.responses.InvestmentResponse;
import com.example.financialSystem.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.Investment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvestmentMapper {
    public Investment toEntity(InvestmentRequest dto) {
        Investment investment = new Investment();
        investment.setValue(dto.value());
        investment.setBaseCurrency(dto.baseCurrency());
        investment.setDateFinancial(dto.dateFinancial());
        investment.setType(dto.investmentType());
        investment.setActionQuantity(dto.actionQuantity());
        investment.setBrokerName(dto.brokerName());
        return investment;
    }

    public List<InvestmentResponse> toDtoList(List<Investment> investments) {
        return investments.stream()
                .map(InvestmentResponse::new)
                .toList();
    }
}

