package main;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by mats on 25.07.2015.
 */
public class TransactionTableRow {

    private SimpleStringProperty bookDate, interestDate, textCode, description, archiveReference, offsetAccount;

    public String getBookDate() {
        return bookDate.get();
    }

    public SimpleStringProperty bookDateProperty() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate.set(bookDate);
    }

    public String getInterestDate() {
        return interestDate.get();
    }

    public SimpleStringProperty interestDateProperty() {
        return interestDate;
    }

    public void setInterestDate(String interestDate) {
        this.interestDate.set(interestDate);
    }

    public String getTextCode() {
        return textCode.get();
    }

    public SimpleStringProperty textCodeProperty() {
        return textCode;
    }

    public void setTextCode(String textCode) {
        this.textCode.set(textCode);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getArchiveReference() {
        return archiveReference.get();
    }

    public SimpleStringProperty archiveReferenceProperty() {
        return archiveReference;
    }

    public void setArchiveReference(String archiveReference) {
        this.archiveReference.set(archiveReference);
    }

    public String getOffsetAccount() {
        return offsetAccount.get();
    }

    public SimpleStringProperty offsetAccountProperty() {
        return offsetAccount;
    }

    public void setOffsetAccount(String offsetAccount) {
        this.offsetAccount.set(offsetAccount);
    }

    public double getValue() {
        return value.get();
    }

    public SimpleDoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    private SimpleDoubleProperty value;

    public TransactionTableRow(Transaction transaction) {
        bookDate = new SimpleStringProperty(transaction.getBookDate().toString());
        interestDate = new SimpleStringProperty(transaction.getInterestDate().toString());
        textCode = new SimpleStringProperty(transaction.getTextCode());
        description = new SimpleStringProperty(transaction.getDescription());
        archiveReference = new SimpleStringProperty(transaction.getArchiveReference());
        offsetAccount = new SimpleStringProperty(transaction.getOffsetAccount());
        value = new SimpleDoubleProperty(transaction.getValue().doubleValue());
    }
}
