package main.utils;

import java.math.BigDecimal;

/**
 * Created by mats on 25.07.2015.
 */
public class NumberUtils {

    public static boolean isPositive(BigDecimal value) {
        return value.intValue() > 0;
    }

    public static boolean isNegative(BigDecimal value) {
        return value.intValue() < 0;
    }

    public static double roundedDivision(BigDecimal dividend, BigDecimal divisor) {
        BigDecimal result = dividend.divide(divisor, BigDecimal.ROUND_HALF_UP);
        result = result.setScale(3, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                }
                else {
                    continue;
                }
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    // Valid format: xxxx.xx.xxxxx
    public static boolean isValidAccountNumber(String accountNumber) {
        String[] accountNumberParts = accountNumber.split("\\.");
        if (accountNumberParts.length != 3) {
            return false;
        }
        for (String part : accountNumberParts) {
            if (!isInteger(part)) {
                return false;
            }
        }
        return (accountNumberParts[0].length() == 4
                && accountNumberParts[1].length() == 2
                && accountNumberParts[2].length() == 5);
    }
}
