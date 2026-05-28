package ru.job4j.deadlock;

public class DifferentOrderDeadlock {
    private final Object left = new Object(); 
    private final Object right = new Object();
    
    public void leftRight() {
        synchronized (left) {
            synchronized (right) {
                System.out.println("smth to do on left");
            }
        }
    }
    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
                System.out.println("smth to do on right");
            }
        }
    }
} 