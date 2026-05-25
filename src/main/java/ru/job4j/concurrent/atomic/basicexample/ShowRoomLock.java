package ru.job4j.concurrent.atomic.basicexample;

/**
 * явное указание:
 * Монитор - это объект ShowRoomLock -- инстанс метод (веернее его кусок)
 * Монитор будет сам класс ShowRoomLock -- класс-мтод
 */

public class ShowRoomLock {
    public void lockOfInstance() { 
         synchronized (this) {
             System.out.println();
         }    
    }

    public static void lockOfClass() {
        synchronized (ShowRoomLock.class) {
            System.out.println();
        }
    }
}