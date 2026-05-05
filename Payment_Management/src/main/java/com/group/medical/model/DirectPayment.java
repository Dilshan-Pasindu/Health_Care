package com.group.medical.model;

public class DirectPayment extends Payment {
    public DirectPayment(double amount, String invoiceId) {
        super(amount, invoiceId);
    }

    @Override
    public double calculateFinalAmount() {
        return amount;
    }

    @Override
    public String getPaymentMethod() {
        return "Direct Payment";
    }
}