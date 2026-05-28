package ru.job4j.threads;

/**
 * Решение 1: не вызывать чужой synchronized-метод, удерживая свой lock
 * Здесь поток сначала отпускает свой lock, и только потом идёт к другому объекту.
 */

public class DeadlockExample {
    static class A {
        public synchronized void callB(B b) {
            System.out.println("A locked by " + Thread.currentThread().getName());
            sleep();

            b.doSomething();
        }

        public synchronized void doSomething() {
            System.out.println("A does something");
        }
    }

    static class B {
        public synchronized void callA(A a) {
            System.out.println("B locked by " + Thread.currentThread().getName());
            sleep();

            a.doSomething();
        }

        public synchronized void doSomething() {
            System.out.println("B does something");
        }
    }

    public static void main(String[] args) {
        A a = new A();
        B b = new B();

        Thread first = new Thread(() -> a.callB(b), "Thread-1");
        Thread second = new Thread(() -> b.callA(a), "Thread-2");

        first.start();
        second.start();
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}