package com.example.financialSystem.controller;

import com.example.financialSystem.model.dto.requests.CostPatchRequest;
import com.example.financialSystem.model.dto.requests.CostRequest;
import com.example.financialSystem.model.dto.responses.CostResponse;
import com.example.financialSystem.service.CostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cost")
public class CostController {

    private final CostService costService;

    public CostController(CostService costService) {
        this.costService = costService;
    }

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

    @GetMapping("/list/me")
    public ResponseEntity<List<CostResponse>> getAllCostByUser() {
        return ResponseEntity.ok().body(costService.listCost());
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<CostResponse>> getAllCost() {
        return ResponseEntity.ok().body(costService.listAllCost());
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<CostResponse> patchCost(@PathVariable int id, @Valid @RequestBody CostPatchRequest costRequest) {
        return ResponseEntity.ok().body(costService.patchCost(id, costRequest));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<String> activateCost(@PathVariable int id) {
        costService.activateCost(id);
        return ResponseEntity.ok().body("Cost activated with id: " + id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCost(@PathVariable int id) {
        costService.deleteCost(id);
        return ResponseEntity.ok().body("Cost with " + id + " success deleted");
    }


}
