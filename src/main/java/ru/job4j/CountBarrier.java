package ru.job4j;

/**
 * Если total = 10, это значит:
 * <p>
 * все потоки, которые вызвали await(), будут ждать, пока суммарно не произойдёт 10 вызовов count().
 *
 * await() — ждёт
 * count() — приближает к снятию блокировки
 */

public class CountBarrier {
    private final Object monitor = this;

    private final int total;

    private int count = 0;

    public CountBarrier(final int total) {
        this.total = total;
    }

    public void count() {
        synchronized (monitor) {
            count++;
            notifyAll();
        }
    }

    public void await() throws InterruptedException {
        synchronized (monitor) {
            while (count < total) {
                wait();
            }
        }
    }
}