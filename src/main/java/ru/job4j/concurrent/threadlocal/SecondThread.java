package ru.job4j.concurrent.threadlocal;

public class SecondThread extends Thread {
    @Override
    public void run() {
        ThreadLocalDemo.threadLocal.set("(2) Second thread");
        System.out.println(ThreadLocalDemo.threadLocal.get());
    }
}
