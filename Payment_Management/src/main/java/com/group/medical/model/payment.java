package com.group.medical.model;

abstract class Payment {
    protected double amount;
    protected String invoiceId;

    public Payment(double amount, String invoiceId) {
        this.amount = amount;
        this.invoiceId = invoiceId;
    }

    public abstract double calculateFinalAmount();
    public abstract String getPaymentMethod();
}