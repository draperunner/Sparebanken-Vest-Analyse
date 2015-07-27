package main;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Created by mats on 21.07.2015.
 */
public class Transaction {

    private LocalDate bookDate, interestDate;
    private String textCode, description, archiveReference, offsetAccount;
    private BigDecimal value;

    public LocalDate getInterestDate() {
        return interestDate;
    }

    public void setInterestDate(LocalDate interestDate) {
        this.interestDate = interestDate;
    }

    public String getTextCode() {
        return textCode;
    }

    public void setTextCode(String textCode) {
        this.textCode = textCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArchiveReference() {
        return archiveReference;
    }

    public void setArchiveReference(String archiveReference) {
        this.archiveReference = archiveReference;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDate bookDate) {
        this.bookDate = bookDate;
    }

    public String getOffsetAccount() {
        return offsetAccount;
    }

    public void setOffsetAccount(String offsetAccount) {
        this.offsetAccount = offsetAccount;
    }

    public YearMonth getYearMonthOfBookDate() {
        return YearMonth.of(bookDate.getYear(), bookDate.getMonth());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Book Date: ");
        stringBuilder.append(bookDate);
        stringBuilder.append(" Interest Date: ");
        stringBuilder.append(interestDate);
        stringBuilder.append(" Text Code: ");
        stringBuilder.append(textCode);
        stringBuilder.append(" Description: ");
        stringBuilder.append(description);
        stringBuilder.append(" Archive Reference: ");
        stringBuilder.append(archiveReference);
        stringBuilder.append(" Value: ");
        stringBuilder.append(value);
        stringBuilder.append(" Offset Account: ");
        stringBuilder.append(offsetAccount);
        return stringBuilder.toString();
    }
}
