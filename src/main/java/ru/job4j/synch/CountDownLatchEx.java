package ru.job4j.synch;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchEx {

    /**
     * Мейн - проснётся только когда все 3 нити дойдут до конца своей полезной части и вызовут countDown().
     * main waits
     * att start, before working: Thread-2 3
     * att start, before working: Thread-1 3
     * att start, before working: Thread-0 3
     * Thread-2 doing smth usefull
     * Thread-1 doing smth usefull
     * Thread-0 doing smth usefull
     * after 1 work: Thread-2 3
     * after 1 work: Thread-1 3
     * after 1 work: Thread-0 3
     * all threads finished their work
     * main starts useful work
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch count = new CountDownLatch(3);
        Runnable task = () -> {
            System.out.println("att start, before working: "
                    + Thread.currentThread().getName() + " " + count.getCount());
            System.out.println(Thread.currentThread().getName() + " " + "doing smth usefull");
            System.out.println("after 1 work: " + Thread.currentThread().getName() + " " + count.getCount());
            count.countDown();
        };
        new Thread(task).start();
        new Thread(task).start();
        new Thread(task).start();

        System.out.println("main waits");

        count.await();

        System.out.println("all threads finished their work");
        System.out.println("main starts useful work");
    }
}
