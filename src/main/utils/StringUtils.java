package main.utils;

/**
 * Created by mats on 30.07.2015.
 */
public class StringUtils {

    public static boolean containsIgnoreCase(String string, String subString) {
        return string.toUpperCase().contains(subString.toUpperCase());
    }

    // Valid format: xxxx.xx.xxxxx
    public static boolean isValidAccountNumber(String accountNumber) {
        String[] accountNumberParts = accountNumber.split("\\.");
        if (accountNumberParts.length != 3) {
            return false;
        }
        for (String part : accountNumberParts) {
            if (!NumberUtils.isInteger(part)) {
                return false;
            }
        }
        return (accountNumberParts[0].length() == 4
                && accountNumberParts[1].length() == 2
                && accountNumberParts[2].length() == 5);
    }
}
