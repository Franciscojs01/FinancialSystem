package com.example.financialSystem.mapper;

import com.example.financialSystem.dto.InvestmentDto;
import com.example.financialSystem.dto.InvestmentRequestDto;
import com.example.financialSystem.model.Investment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvestmentMapper {
    public Investment toEntity(InvestmentRequestDto dto) {
        Investment investment = new Investment();
        investment.setValue(dto.value());
        investment.setBaseCurrency(dto.baseCurrency());
        investment.setDateFinancial(dto.dateFinancial());
        investment.setType(dto.type());
        investment.setActionQuantity(dto.actionQuantity());
        investment.setBrokerName(dto.brokerName());
        return investment;
    }

    public InvestmentDto toDto(Investment investment) {
        return new InvestmentDto(investment);
    }

    public List<InvestmentDto> toDtoList(List<Investment> investments) {
        return investments.stream()
                .map(InvestmentDto::new)
                .toList();
    }
}

