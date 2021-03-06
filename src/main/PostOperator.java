package main;

import main.utils.CollectionUtils;

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
        .orElse(BigDecimal.ZERO);

    PostOperator defaultAverageOperator = t -> CollectionUtils.getAverage(t.stream()
            .map(Transaction::getValue)
            .collect(Collectors.toList()));

    PostOperator defaultMedianOperator = t -> CollectionUtils.getMedian(t.stream()
            .map(Transaction::getValue)
            .collect(Collectors.toList()));

    /**
     * @param transactions
     * @return the result of applying an operation on the given elements
     */
    BigDecimal calculate(List<Transaction> transactions);

}
