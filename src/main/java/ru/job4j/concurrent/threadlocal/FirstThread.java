package ru.job4j.concurrent.threadlocal;

public class FirstThread extends Thread {
    @Override
    public void run() {
        ThreadLocalDemo.threadLocal.set("(1) First thread");
        System.out.println(ThreadLocalDemo.threadLocal.get());
    }
}
