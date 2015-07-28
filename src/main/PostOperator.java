package main;

import main.utils.ListUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mats on 26.07.2015.
 */
public interface PostOperator {

    PostOperator defaultTotalOperator = t -> t.stream()
        .map(Transaction::getValue)
        .reduce(BigDecimal::add)
        .orElse(new BigDecimal(0));

    PostOperator defaultAverageOperator = t -> ListUtils.getAverage(t.stream()
        .map(Transaction::getValue)
        .collect(Collectors.toList()));

    PostOperator defaultMedianOperator = t -> ListUtils.getMedian(t.stream()
        .map(Transaction::getValue)
        .collect(Collectors.toList()));

    /**
     * @param transactions
     * @return the result of applying an operation on the given elements
     */
    BigDecimal calculate(List<Transaction> transactions);

}
