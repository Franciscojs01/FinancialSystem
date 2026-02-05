package com.example.financialSystem.controller;


import com.example.financialSystem.model.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.model.dto.requests.InvestmentRequest;
import com.example.financialSystem.model.dto.responses.InvestmentResponse;
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

    @PostMapping("/create")
    public ResponseEntity<InvestmentResponse> create(@Valid @RequestBody InvestmentRequest investmentRequest) {
        return ResponseEntity.ok().body(investmentService.createInvestment(investmentRequest));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<InvestmentResponse> edit(@PathVariable int id, @Valid @RequestBody InvestmentRequest investmentRequest) {
        return ResponseEntity.ok().body(investmentService.updateInvestment(id,  investmentRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentResponse> getInvestment(@PathVariable int id) {
        return ResponseEntity.ok().body(investmentService.getInvestmentById(id));
    }

    @GetMapping("/{id}/simulate")
    public ResponseEntity<InvestmentResponse> simulateInvestment(@PathVariable int id, @RequestParam int days) {
        return ResponseEntity.ok().body(investmentService.simulateInvestment(id, days));
    }

    @GetMapping("/list/me")
    public ResponseEntity<List<InvestmentResponse>> getAllInvestmentByUser() {
        return ResponseEntity.ok().body(investmentService.listInvestments());
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<InvestmentResponse>> getAllInvestment() {
        return ResponseEntity.ok().body(investmentService.listAllInvestments());
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<InvestmentResponse> patchInvestment(@PathVariable int id, @RequestBody InvestmentPatchRequest patchRequest) {
        return  ResponseEntity.ok().body(investmentService.patchInvestment(id,  patchRequest));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<String> activateInvestment(@PathVariable int id) {
        investmentService.activateInvestment(id);
        return ResponseEntity.ok("Investment activated with id: " + id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInvestment(@PathVariable int id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.ok("Investment success deleted with id: " + id);
    }


}
