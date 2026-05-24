package ru.job4j.concurrent.threadlocal;

public class ThreadLocalDemo {
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        Thread first = new FirstThread();
        Thread second = new SecondThread();
        System.out.println("before: " + threadLocal.get());
        threadLocal.set("main thread");
        System.out.println("after: " + threadLocal.get());
        first.start();
        second.start();
        first.join();
        second.join();
    }
}
