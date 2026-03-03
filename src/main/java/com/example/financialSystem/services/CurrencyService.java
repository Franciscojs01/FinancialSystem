package com.example.financialSystem.services;


import com.example.financialSystem.models.dto.responses.CoinFastForexResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyService {
    @Value("${fastforex.api.key}")
    private String apiKey;

    @Value("${fastforex.api.url}")
    private String apiUrl;

    public Double getLiveRates(String currencyCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CoinFastForexResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    CoinFastForexResponse.class
            );

            CoinFastForexResponse body = response.getBody();

            if (body != null && body.getResults() != null) {
                return body.getResults().get(currencyCode);
            }
        } catch (Exception e) {
            System.err.println("Error searching for rate in external API : " + e.getMessage());
        }

        return null;
    }
}
