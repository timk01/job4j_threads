package ru.job4j.synch;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Некий аналог джойна, который собирает потоки после того как все отработали
 * Thread-0 started work
 * Thread-1 started work
 * Thread-2 started work
 *
 * Thread-0 waiting at barrier
 * Thread-1 waiting at barrier
 * Thread-2 waiting at barrier
 *
 * Все нити дошли до барьера. Можно продолжать. - ЭТО КЛЮЧЕВОЕ
 *
 * Thread-2 continues work after barrier
 * Thread-0 continues work after barrier
 * Thread-1 continues work after barrier
 */

public class CyclicBarrierEx {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, () ->
                System.out.println("Все нити дошли до барьера. Можно продолжать.")
        );

        Runnable task = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " started work");

                Thread.sleep(1000);

                System.out.println(Thread.currentThread().getName() + " waiting at barrier");
                barrier.await();

                System.out.println(Thread.currentThread().getName() + " continues work after barrier");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        new Thread(task).start();
        new Thread(task).start();
        new Thread(task).start();
    }
}