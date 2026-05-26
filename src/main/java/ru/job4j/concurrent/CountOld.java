package ru.job4j.concurrent;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class CountOld {

    @GuardedBy("this")
    private int value;

    public synchronized void simpleIncrement() {
        this.value++;
    }

    public synchronized int get() {
        return this.value;
    }

    public int increment() {
        synchronized (this) {
            this.value++;
            return this.value;
        }
    }
}
