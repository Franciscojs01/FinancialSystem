package com.example.financialSystem.controller;


import com.example.financialSystem.dto.InvestmentDto;
import com.example.financialSystem.model.Investment;
import com.example.financialSystem.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investments")
public class InvestmentController {
    @Autowired
    InvestmentService investmentService;

    @PostMapping("/create")
    public ResponseEntity<InvestmentDto> create(@RequestBody Investment investment) {
        InvestmentDto investmentCreated = investmentService.createInvestment(investment);
        return ResponseEntity.ok().body(investmentCreated);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<InvestmentDto> edit(@PathVariable int id, @RequestBody InvestmentDto investment) {
        InvestmentDto modifiedInvestment = investmentService.editInvestment(id, investment);
        return ResponseEntity.ok().body(modifiedInvestment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentDto> getInvestment(@PathVariable int id) {
        return ResponseEntity.ok().body(investmentService.getInvestmentById(id));
    }
}
