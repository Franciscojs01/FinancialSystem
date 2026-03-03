package com.example.financialSystem.services;

import com.example.financialSystem.models.enums.SupportedCrypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CryptoService {
    @Value("${coingecko.api.url}")
    private String apiUrl;

    public Double getCryptoPrice(String coinGeckoId, String fiatCurrency) {
        RestTemplate restTemplate = new RestTemplate();
        String fiatLower = fiatCurrency.toLowerCase();

        String url = String.format("%s/simple/price?ids=%s&vs_currencies=%s",
                apiUrl, coinGeckoId, fiatLower);

        try {
            ResponseEntity<Map<String, Map<String, Double>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {}
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
