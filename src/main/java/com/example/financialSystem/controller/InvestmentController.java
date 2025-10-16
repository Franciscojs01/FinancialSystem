package com.example.financialSystem.controller;


import com.example.financialSystem.dto.InvestmentDto;
import com.example.financialSystem.dto.InvestmentRequestDto;
import com.example.financialSystem.mapper.InvestmentMapper;
import com.example.financialSystem.model.Investment;
import com.example.financialSystem.service.InvestmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investments")
public class InvestmentController{
    @Autowired
    InvestmentService investmentService;

    @Autowired
    InvestmentMapper investmentMapper;

    @PostMapping("/create")
    public ResponseEntity<InvestmentDto> create(@Valid @RequestBody InvestmentRequestDto investmentDto) {
        Investment investment = investmentMapper.toEntity(investmentDto);
        InvestmentDto investmentCreated = investmentService.createInvestment(investment);
        return ResponseEntity.ok().body(investmentCreated);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<InvestmentDto> edit(@PathVariable int id, @RequestBody InvestmentRequestDto investmentRequestDto) {
        InvestmentDto modifiedInvestment = investmentService.editInvestment(id, investmentRequestDto);
        return ResponseEntity.ok().body(modifiedInvestment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentDto> getInvestment(@PathVariable int id) {
        return ResponseEntity.ok().body(investmentService.getInvestmentById(id));
    }

    @GetMapping("/{id}/simulate")
    public ResponseEntity<InvestmentDto> simulateInvestment(@PathVariable int id, @RequestParam int days) {
        InvestmentDto simulated = investmentService.simulateInvestment(id, days);
        return ResponseEntity.ok().body(simulated);
    }

    @GetMapping("/list")
    public ResponseEntity<List<InvestmentDto>> getInvestments() {
        List<InvestmentDto> investments = investmentMapper.toDtoList(investmentService.listInvestments());

        return ResponseEntity.ok(investments);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInvestment(@PathVariable int id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.ok("Investment success deleted! ");
    }
}
