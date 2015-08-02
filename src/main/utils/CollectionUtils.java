package main.utils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mats on 26.07.2015.
 */
public class CollectionUtils {

    public static BigDecimal getAverage(List<BigDecimal> numbers) {
        if (numbers.isEmpty()) {
            return new BigDecimal(0);
        }
        return numbers.stream().reduce(BigDecimal::add).get().divide(new BigDecimal(numbers.size()), BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal getMedian(List<BigDecimal> numbers) {
        if (numbers.isEmpty()) {
            return new BigDecimal(0);
        }
        numbers.sort(BigDecimal::compareTo);
        if (numbers.size() % 2 == 0) {
            int i = numbers.size() / 2;
            return numbers.get(i - 1).add(numbers.get(i));
        }
        return numbers.get((numbers.size() - 1) / 2);
    }

}
