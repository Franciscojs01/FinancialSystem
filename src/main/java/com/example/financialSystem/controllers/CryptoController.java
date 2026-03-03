package com.example.financialSystem.controllers;

import com.example.financialSystem.models.enums.SupportedCrypto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cryptos")
@Tag(name = "crypto", description = "Endpoints for retrieving supported cryptocurrencies")
public class CryptoController {
    @ApiResponse(responseCode = "200", description = "List of supported cryptocurrencies retrieved successfully")
    @GetMapping("/supported")
    public List<Map<String, String>> getSupportedCryptos() {
        return Arrays.stream(SupportedCrypto.values())
                .map(crypto -> Map.of(
                        "symbol", crypto.name(),
                        "displayName", crypto.getDisplayName()
                ))
                .collect(Collectors.toList());
    }
}
