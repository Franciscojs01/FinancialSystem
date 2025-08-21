package com.example.financialSystem.controller;

import com.example.financialSystem.dto.InvestmentDto;
import com.example.financialSystem.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/investments")
public class InvestmentController {
    @Autowired
    InvestmentService investmentService;

    @PostMapping("/create")
    public ResponseEntity<InvestmentDto> create(@RequestBody InvestmentDto investmentDto) {
        InvestmentDto investmentCreated = investmentService.createInvestment(investmentDto);
        return ResponseEntity.ok().body(investmentCreated);
    }
}
