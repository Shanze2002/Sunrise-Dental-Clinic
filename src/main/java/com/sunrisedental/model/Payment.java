package com.sunrisedental.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean / DTO: Payment
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int paymentId;
    private int billId;
    private String receiptNumber;
    private Timestamp paymentDate;
    private double amount;
    private String paymentMethod; // Cash, Credit Card, Debit Card, Bank Transfer, Insurance
    private Integer cashierId;
    private String cashierName; // from user
    private String transactionReference;
    private String remarks;

    public Payment() {}

    public Payment(int billId, String receiptNumber, double amount, String paymentMethod, Integer cashierId, String transactionReference, String remarks) {
        this.billId = billId;
        this.receiptNumber = receiptNumber;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.cashierId = cashierId;
        this.transactionReference = transactionReference;
        this.remarks = remarks;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getCashierId() {
        return cashierId;
    }

    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
