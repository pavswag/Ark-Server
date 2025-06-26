package io.kyros.sql.ingamestore;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 13/12/2023
 */

@Getter
@Setter
public class Item {
    private int id;
    private String name;
    private double price;

    public Item(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        // Calculate discounted price if discountCode is valid and not expired
        double discountedPrice = price;

        if (Configuration.getDiscountCode() != null && isValidDiscount()) {
            double discountMultiplier = 1 - (Configuration.getDiscountCode().getPercentage() / 100.0);
            discountedPrice = price * discountMultiplier;
        }

        // Round to two decimal places using BigDecimal
        BigDecimal roundedPrice = new BigDecimal(discountedPrice).setScale(2, RoundingMode.HALF_UP);
        return roundedPrice.doubleValue();
    }

    private boolean isValidDiscount() {
        // Implement your logic to check if the discountCode is valid and not expired
        // For example, you can check against a list of valid discount codes and expiry dates
        return Configuration.getDiscountCode() != null && !Configuration.getDiscountCode().isExpired() && Configuration.getDiscountCode().getPercentage() > 0;
    }
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
