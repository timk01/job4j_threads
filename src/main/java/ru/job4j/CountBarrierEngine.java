package ru.job4j;

/**
 * запустил 3 потока, всее 3 ждут (и все) - пока общий каунт не сравняется с локальным
 * <p>
 * barrier.count(); - аждый приближает это
 * ккак только стало = 3, возобновляется работа методов (т.е. count >= total - из цикла вылетем и освободим монитор).
 * как-то так
 */

public class CountBarrierEngine {
    public static void main(String[] args) {
        CountBarrier barrier = new CountBarrier(10);
        Thread thread1 = new Thread(
                () -> {
                    System.out.println(Thread.currentThread().getName() + " started");
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                "thread1"
        );
        Thread thread2 = new Thread(
                () -> {
                    System.out.println(Thread.currentThread().getName() + " started");
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                "thread2"
        );
        Thread thread3 = new Thread(
                () -> {
                    System.out.println(Thread.currentThread().getName() + " started");
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                "thread3"
        );

        thread1.start();
        thread2.start();
        thread3.start();

        System.out.println("counting till start");
        barrier.count();
        barrier.count();
        barrier.count();

    }
}
