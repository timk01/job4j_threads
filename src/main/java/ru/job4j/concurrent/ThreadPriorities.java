package ru.job4j.concurrent;

public class ThreadPriorities {
    public static void main(String[] args) throws InterruptedException {
        Thread first = new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        System.out.println("first");
                    }
                }
        );
        System.out.println(first.getPriority());
        first.setPriority(1);
        System.out.println(first.getPriority());

        Thread second = new Thread(
                () -> {
                    for (int i = 0; i < 10; i++) {
                        System.out.println("second");
                    }
                }
        );
        second.setPriority(10);

        first.start();
        second.start();

        first.join();
        second.join();
    }
}
