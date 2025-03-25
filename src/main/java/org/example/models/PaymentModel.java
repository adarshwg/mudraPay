package org.example.models;

public class PaymentModel {
    public final String receiver;
    public final int amount;

    public PaymentModel(String receiver, int amount) {
        this.receiver = receiver;
        this.amount = amount;
    }
}
