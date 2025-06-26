package io.kyros.content.tradingpost;

import java.util.HashMap;
import java.util.Map;

public class PriceParser {

    private static final Map<Character, Long> suffixMap = new HashMap<>();

    static {
        suffixMap.put('k', 1_000L);
        suffixMap.put('m', 1_000_000L);
        suffixMap.put('b', 1_000_000_000L);
        suffixMap.put('t', 1_000_000_000_000L);
        suffixMap.put('q', 1_000_000_000_000_000L);  // For quadrillion
        suffixMap.put('Q', 1_000_000_000_000_000_000L);  // For quintillion
    }

    public static long parsePrice(String priceStr) {
        priceStr = priceStr.trim().toLowerCase();  // Normalize input

        // Check if the last character is a known suffix
        char lastChar = priceStr.charAt(priceStr.length() - 1);
        if (suffixMap.containsKey(lastChar)) {
            // Parse the number part and multiply by the suffix multiplier
            try {
                long baseValue = Long.parseLong(priceStr.substring(0, priceStr.length() - 1));
                return baseValue * suffixMap.get(lastChar);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + priceStr);
            }
        } else {
            // If there's no suffix, just parse the whole string as a long value
            try {
                return Long.parseLong(priceStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + priceStr);
            }
        }
    }
}