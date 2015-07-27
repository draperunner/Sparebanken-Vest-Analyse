package main;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

/**
 * Created by mats on 25.07.2015.
 */
public class MonthlyOverview {

    private SimpleStringProperty month;
    private SimpleDoubleProperty balance, expenses, income, groceriesExpenses;

    public MonthlyOverview() {}

    public MonthlyOverview(String month, BigDecimal balance, BigDecimal expenses, BigDecimal income, BigDecimal groceriesExpenses) {
        this.month = new SimpleStringProperty(month);
        this.balance = new SimpleDoubleProperty(balance.doubleValue());
        this.expenses = new SimpleDoubleProperty(expenses.doubleValue());
        this.income = new SimpleDoubleProperty(income.doubleValue());
        this.groceriesExpenses = new SimpleDoubleProperty(groceriesExpenses.doubleValue());
    }

    public SimpleStringProperty monthProperty() {
        return month;
    }

    public String getMonth() {
        return month.get();
    }

    public void setMonth(String month) {
        this.month.set(month);
    }

    public double getBalance() {
        return balance.get();
    }

    public SimpleDoubleProperty balanceProperty() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public double getExpenses() {
        return expenses.get();
    }

    public SimpleDoubleProperty expensesProperty() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses.set(expenses);
    }

    public double getIncome() {
        return income.get();
    }

    public SimpleDoubleProperty incomeProperty() {
        return income;
    }

    public void setIncome(double income) {
        this.income.set(income);
    }

    public double getGroceriesExpenses() {
        return groceriesExpenses.get();
    }

    public SimpleDoubleProperty groceriesExpensesProperty() {
        return groceriesExpenses;
    }

    public void setGroceriesExpenses(double groceriesExpenses) {
        this.groceriesExpenses.set(groceriesExpenses);
    }
}
