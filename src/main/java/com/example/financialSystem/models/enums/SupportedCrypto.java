package com.example.financialSystem.models.enums;

import lombok.Getter;

@Getter
public enum SupportedCrypto {
    BTC("Bitcoin", "bitcoin"),
    ETH("Ethereum", "ethereum"),
    SOL("Solana", "solana"),
    ADA("Cardano", "cardano"),
    DOGE("Dogecoin", "dogecoin");

    private final String displayName;
    private final String coinGeckoId;

    SupportedCrypto(String displayName, String coinGeckoId) {
        this.displayName = displayName;
        this.coinGeckoId = coinGeckoId;
    }
}
