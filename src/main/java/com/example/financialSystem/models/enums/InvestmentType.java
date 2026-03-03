package com.example.financialSystem.models.enums;

import lombok.Getter;

@Getter
public enum InvestmentType {
    STOCK(null, null),
    FUND(null, null),
    FIXED_INCOME(0.08, null),
    TREASURY(0.10, null),

    CRYPTO_BTC(null, "bitcoin"),
    CRYPTO_ETH(null, "ethereum"),
    CRYPTO_SOL(null, "solana"),
    CRYPTO_DOGE(null, "dogecoin");

    private final Double rate;
    private final String coinGeckoId;

    InvestmentType(Double rate, String coinGeckoId) {
        this.rate = rate;
        this.coinGeckoId = coinGeckoId;
    }

    public boolean isCrypto() {
        return this.coinGeckoId != null;
    }

}
