package com.example.financialSystem.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InterestCalculator {

    /**
     * @param principal initial amount
     * @param annualRate annual interest rate (e.g. 0.1365 for 13.65%)
     * @param days number of days invested
     * @return accumulated amount
     */
    public static BigDecimal calculate(BigDecimal principal, double annualRate, int days) {
        // convert annual rate to daily rate (based on 252 business days in Brazilian financial market)
        double dailyRate = Math.pow(1 + annualRate, 1.0 / 252) - 1;

        double amount = principal.doubleValue() * Math.pow(1 + dailyRate, days);

        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }
}
