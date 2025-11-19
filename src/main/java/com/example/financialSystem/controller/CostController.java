package com.example.financialSystem.controller;

import com.example.financialSystem.dto.requests.CostRequest;
import com.example.financialSystem.dto.responses.CostResponse;
import com.example.financialSystem.service.CostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost")
public class CostController {
    @Autowired
    CostService costService;

    @PostMapping("/create")
    public ResponseEntity<CostResponse> createCost(@Valid @RequestBody CostRequest costRequest) {
        return ResponseEntity.ok().body(costService.createCost(costRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CostResponse> getCost(@PathVariable int id) {
        return ResponseEntity.ok().body(costService.getCostById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<CostResponse> editCost(@PathVariable int id, @Valid @RequestBody CostRequest costRequest) {
        return ResponseEntity.ok().body(costService.updateCost(id, costRequest));
    }




}
