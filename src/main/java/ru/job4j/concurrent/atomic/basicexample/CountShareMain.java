package ru.job4j.concurrent.atomic.basicexample;

public class CountShareMain {
    public static void main(String[] args) throws InterruptedException {
        CountSynchronized count = new CountSynchronized();
        Thread first = new Thread(
          count::increment
        );
        Thread second = new Thread(
                () -> count.increment()
        );

        first.start();
        second.start();
        first.join();
        second.join();

        System.out.println(count.get());
    }
}
