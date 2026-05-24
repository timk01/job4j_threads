package ru.job4j.concurrent;

public class DemonThread {
    public static void main(String[] args) {
        Thread demonThread = new Thread();
        demonThread.setDaemon(true);
        demonThread.start();
        System.out.println(demonThread.isDaemon());
    }
}
