package ru.job4j.deadlock;

public class DifferentOrderDeadlockSolution {
    private final Object left = new Object(); 
    private final Object right = new Object();

    /**
     * если локи независимы - можно разнести
     * если должны одновременно проявляться - тут уже думтаь надо, т.к. ресурсов будет 2 (мб проблемы), но в общем виде
     * брать оба лока
     */

    public void leftRight() {
        synchronized (left) {
                System.out.println("smth to do on left");
        }
        synchronized (right) {
            System.out.println("smth to do on right");
        }
    }
    public void rightLeft() {
        synchronized (right) {
            System.out.println("smth to do on right");
        }
        synchronized (left) {
            System.out.println("smth to do on left");
        }
    }
} 