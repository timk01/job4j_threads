package ru.job4j.threads;

public record Account(int id, int amount) {
    public <Amount> Comparable<Amount> getBalance() {
        return null;
    }

    public <Amount> void debit(Amount amount) {
    }

    public <Amount> void credit(Amount amount) {
    }

    public int getId() {
        return 0;
    }
}