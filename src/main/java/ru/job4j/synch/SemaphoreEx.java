package ru.job4j.synch;

import java.util.concurrent.Semaphore;

public class SemaphoreEx {

    /**
     * новая нить заходит в task, упирается в acquire() - потому что разрешений изначально 0, ждёт разрешение;
     * main через 3 секунды делает release(1);
     * рабочая нить получает permit, выполняет работу и возвращает permit обратно.
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Runnable task = () -> {
            try {
                semaphore.acquire();
                System.out.println("Нить выполнила задачу");
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(task).start();
        Thread.sleep(3000);
        semaphore.release(1);
    }
}
