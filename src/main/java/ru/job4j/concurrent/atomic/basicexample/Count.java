package ru.job4j.concurrent.atomic.basicexample;

public class Count {
    private int value;

    public void increment() {
        value++;
    }

    public int get() {
        return value;
    }
}