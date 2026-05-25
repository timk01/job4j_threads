package ru.job4j.concurrent.atomic.basicexample;

/**
 * потоки будут менять только после полного выхода первого потока из synchronized-метода (полный лок метода)
 * == this.value++;
 * или:
 * synchronized(this) {
 *     value++;
 * }
 * если мешаем лок не кусок метода (и там и там, инстанс-методы, но во втором гибче на кусок метода, а не на весь)
 *
 */

public class CountSynchronized {
    private int value;

    public synchronized void increment() {
        value++;
    }

    public synchronized int get() {
        return value;
    }
}
