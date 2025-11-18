package com.example.financialSystem.controller;

import com.example.financialSystem.dto.requests.CostRequest;
import com.example.financialSystem.dto.responses.CostResponse;
import com.example.financialSystem.service.CostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cost")
public class CostController {
    @Autowired
    CostService costService;

    @PostMapping("/create")
    public ResponseEntity<CostResponse> createCost(@Valid @RequestBody CostRequest costRequest) {
        return ResponseEntity.ok().body(costService.createCost(costRequest));
    }

}
