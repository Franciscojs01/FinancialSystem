package com.example.financialSystem.controller;


import com.example.financialSystem.dto.InvestmentPatchRequest;
import com.example.financialSystem.dto.InvestmentResponse;
import com.example.financialSystem.dto.InvestmentRequest;
import com.example.financialSystem.mapper.InvestmentMapper;
import com.example.financialSystem.model.Expense;
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
    public ResponseEntity<InvestmentResponse> create(@Valid @RequestBody InvestmentRequest investmentRequest) {
        Investment investment = investmentMapper.toEntity(investmentRequest);
        InvestmentResponse createdInvestment = investmentService.createInvestment(investment);
        return ResponseEntity.ok().body(createdInvestment);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<InvestmentResponse> edit(@PathVariable int id, @Valid @RequestBody InvestmentRequest investmentDto) {
        Investment investment = investmentMapper.toEntity(investmentDto);

        InvestmentResponse updatedInvestment = investmentService.editInvestment(id, investment);

        return ResponseEntity.ok().body(updatedInvestment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentResponse> getInvestment(@PathVariable int id) {
        return ResponseEntity.ok().body(investmentService.getInvestmentById(id));
    }

    @GetMapping("/{id}/simulate")
    public ResponseEntity<InvestmentResponse> simulateInvestment(@PathVariable int id, @RequestParam int days) {
        InvestmentResponse simulated = investmentService.simulateInvestment(id, days);
        return ResponseEntity.ok().body(simulated);
    }

    @GetMapping("/list")
    public ResponseEntity<List<InvestmentResponse>> getInvestments() {
        List<InvestmentResponse> investments = investmentMapper.toDtoList(investmentService.listInvestments());
        return ResponseEntity.ok(investments);
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<InvestmentResponse> patchInvestment(@PathVariable int id, @RequestBody InvestmentPatchRequest patchRequest) {
        InvestmentResponse updateInvestment =  investmentService.patchInvestment(id, patchRequest);
        return  ResponseEntity.ok().body(updateInvestment);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInvestment(@PathVariable int id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.ok("Investment success deleted! ");
    }


}
