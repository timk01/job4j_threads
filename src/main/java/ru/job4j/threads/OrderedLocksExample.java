package ru.job4j.threads;

/**
 * если нужно держать оба lock-а — брать их всегда в одном порядке
 */

public class OrderedLocksExample {
    static class A {
    }

    static class B {
    }

    public static void doWork(A a, B b) {
        synchronized (a) {
            synchronized (b) {
                System.out.println("Work with A and B");
            }
        }
    }

    public static void doAnotherWork(A a, B b) {
        synchronized (a) {
            synchronized (b) {
                System.out.println("Another work with A and B");
            }
        }
    }
}