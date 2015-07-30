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

    public static BigDecimal roundedDivision(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, 4, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal roundedDivision(BigDecimal dividend, BigDecimal divisor, BigDecimal resultIfDivisorIsZero) {
        if (divisor.equals(BigDecimal.ZERO)) {
            return resultIfDivisorIsZero;
        }
        return roundedDivision(dividend, divisor);
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
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
            if (Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
