package com.example.financialSystem.mapper;

import com.example.financialSystem.dto.requests.CostPatchRequest;
import com.example.financialSystem.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.dto.responses.InvestmentResponse;
import com.example.financialSystem.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.Cost;
import com.example.financialSystem.model.Investment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
        investment.setDaysInvested((int) ChronoUnit.DAYS.between(investment.getDateFinancial(), LocalDate.now()));
        return investment;
    }

    public void toPatchEntity(InvestmentPatchRequest patchRequest) {
        Investment investment = new Investment();
        investment.setValue(patchRequest.getValue());
        investment.setBaseCurrency(patchRequest.getBaseCurrency());
        investment.setDateFinancial(patchRequest.getDateFinancial());
        investment.setType(patchRequest.getInvestmentType());
        investment.setActionQuantity(patchRequest.getActionQuantity());
        investment.setBrokerName(patchRequest.getBrokerName());

    }

    public List<InvestmentResponse> toDtoList(List<Investment> investments) {
        return investments.stream()
                .map(InvestmentResponse::new)
                .toList();
    }
}

