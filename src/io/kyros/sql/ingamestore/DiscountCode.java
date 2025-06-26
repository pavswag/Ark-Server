package io.kyros.sql.ingamestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 12/12/2023
 */
public class DiscountCode {
    private String code;
    private int percentage;
    private String expiryDate;

    public DiscountCode(String code, int percentage, String expiryDate) {
        this.code = code;
        this.percentage = percentage;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        if (expiryDate == null || expiryDate.isEmpty()) {
            // No expiry date, consider it not expired
            return false;
        }

        // Parse expiry date
        LocalDate expiryLocalDate = LocalDate.parse(expiryDate, DateTimeFormatter.ISO_DATE);

        // Compare with the current date
        LocalDate currentDate = LocalDate.now();

        return currentDate.isAfter(expiryLocalDate);
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "DiscountCode{" +
                "code='" + code + '\'' +
                ", percentage=" + percentage +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }
}