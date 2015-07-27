package main;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mats on 26.07.2015.
 */
public class ListUtils {

    public static BigDecimal getAverage(List<BigDecimal> numbers) {
        return numbers.stream().reduce(BigDecimal::add).get().divide(new BigDecimal(numbers.size()), BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal getMedian(List<BigDecimal> numbers) {
        numbers.sort(BigDecimal::compareTo);
        if (numbers.size() % 2 == 0) {
            int i = numbers.size() / 2;
            return numbers.get(i - 1).add(numbers.get(i));
        }
        return numbers.get((numbers.size() - 1) / 2);
    }

}
