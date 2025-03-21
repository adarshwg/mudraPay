package org.example.models;

public record Transaction(String transactionId, String sender, String receiver, int amount, long epochTime) {
}
