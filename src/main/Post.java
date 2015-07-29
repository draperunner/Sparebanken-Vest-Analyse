package main;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mats on 26.07.2015.
 *
 */
public class Post {

    public enum Type {OTHER, EXPENSE, INCOME}

    private String name;
    private String norwegianName;
    private Type type;
    private boolean showAsPositive = false;
    private List<Transaction> transactions;
    private PostFilterer filterer;
    private PostOperator totalOperator, averageOperator, medianOperator;

    public Post(String name, String norwegianName, Type type, List<Transaction> transactions, PostFilterer filterer) {
        this.name = name;
        this.norwegianName = norwegianName;
        this.type = type;
        showAsPositive = type == Type.EXPENSE;
        this.transactions = transactions;
        this.filterer = filterer;
        this.totalOperator = PostOperator.defaultTotalOperator;
        this.averageOperator = PostOperator.defaultAverageOperator;
        this.medianOperator = PostOperator.defaultMedianOperator;
    }

    public void setFilterer(PostFilterer filterer) {
        this.filterer = filterer;
    }

    public void setTotalOperator(PostOperator totalOperator) {
        this.totalOperator = totalOperator;
    }

    public void setAverageOperator(PostOperator averageOperator) {
        this.averageOperator = averageOperator;
    }

    public void setMedianOperator(PostOperator medianOperator) {
        this.medianOperator = medianOperator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNorwegianName() {
        return norwegianName;
    }

    public void setNorwegianName(String norwegianName) {
        this.norwegianName = norwegianName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean getShowAsPositive() {
        return showAsPositive;
    }

    public void setShowAsPositive(boolean showAsPositive) {
        this.showAsPositive = showAsPositive;
    }

    public List<Transaction> filter(List<Transaction> transactions) {
        return filterer.filter(transactions);
    }

    public BigDecimal getTotal() {
        return getTotal(filter(transactions));
    }

    public BigDecimal getTotal(List<Transaction> transactions) {
        return totalOperator.calculate(filter(transactions));
    }

    public BigDecimal getAverage() {
        return getAverage(filter(transactions));
    }

    public BigDecimal getAverage(List<Transaction> transactions) {
        return averageOperator.calculate(filter(transactions));
    }

    public BigDecimal getMedian() {
        return getMedian(filter(transactions));
    }

    public BigDecimal getMedian(List<Transaction> transactions) {
        return medianOperator.calculate(filter(transactions));
    }
}
