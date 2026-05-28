package ru.job4j.threads;

/**
 * Решение 1: не вызывать чужой synchronized-метод, удерживая свой lock
 */

public class NoDeadlockExample {
    static class A {
        public void callB(B b) {
            synchronized (this) {
                System.out.println("A locked by " + Thread.currentThread().getName());
            }

            b.doSomething();
        }

        public synchronized void doSomething() {
            System.out.println("A does something");
        }
    }

    static class B {
        public void callA(A a) {
            synchronized (this) {
                System.out.println("B locked by " + Thread.currentThread().getName());
            }

            a.doSomething();
        }

        public synchronized void doSomething() {
            System.out.println("B does something");
        }
    }
}