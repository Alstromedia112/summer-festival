package com.me1q.summerFestival.currency;

import java.text.DecimalFormat;

public class CurrencyFormatter {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");
    private static final String CURRENCY_SYMBOL = "円";

    public static String format(int amount) {
        return FORMATTER.format(amount) + CURRENCY_SYMBOL;
    }
}

