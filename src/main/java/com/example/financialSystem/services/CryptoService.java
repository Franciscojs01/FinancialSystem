package com.example.financialSystem.services;

import com.example.financialSystem.models.enums.SupportedCrypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CryptoService {
    @Value("${coingecko.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Double getCryptoPrice(String coinGeckoId, String fiatCurrency) {
        String fiatLower = fiatCurrency.toLowerCase();
        String url = String.format("%s/simple/price?ids=%s&vs_currencies=%s",
                apiUrl, coinGeckoId, fiatLower);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-cg-pro-api-key", apiUrl);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Map<String, Double>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            Map<String, Map<String, Double>> body = response.getBody();

            if (body != null && body.containsKey(coinGeckoId)) {
                Map<String, Double> prices = body.get(coinGeckoId);
                if (prices != null && prices.containsKey(fiatLower)) {
                    return prices.get(fiatLower);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching crypto price from API: " + e.getMessage());
        }

        return null;
    }
}
