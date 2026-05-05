package com.group.medical.model;

public class InsurancePayment extends Payment {
    public InsurancePayment(double amount, String invoiceId) {
        super(amount, invoiceId);
    }

    @Override
    public double calculateFinalAmount() {
        return amount * 0.8; // 20% discount example
    }

    @Override
    public String getPaymentMethod() {
        return "Insurance Payment";
    }
}