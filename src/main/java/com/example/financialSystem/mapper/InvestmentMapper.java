package com.example.financialSystem.mapper;

import java.util.List;
import com.example.financialSystem.dto.InvestmentDto;
import com.example.financialSystem.dto.InvestmentRequestDto;
import com.example.financialSystem.model.Investment;
import org.springframework.stereotype.Component;

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
        InvestmentDto dto = new InvestmentDto();
        dto.setValue(investment.getValue());
        dto.setBaseCurrency(investment.getBaseCurrency());
        dto.setDateFinancial(investment.getDateFinancial());
        dto.setType(investment.getType());
        dto.setActionQuantity(investment.getActionQuantity());
        dto.setBrokerName(investment.getBrokerName());
        dto.setCurrentValue(investment.getCurrentValue());
        return dto;
    }

    public List<InvestmentDto> toDtoList(List<Investment> investments) {
        return investments.stream()
                .map(this::toDto)
                .toList();
    }
}
