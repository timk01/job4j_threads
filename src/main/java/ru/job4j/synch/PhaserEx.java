package ru.job4j.synch;

import java.util.concurrent.Phaser;

/**
 * <p>
 * ГЛАВНОЕ: Phaser можно воспринимать как более гибкий CyclicBarrier,
 * где можно делать несколько фаз и менять количество участников.
 * <p>
 * <p>
 * в Phaser уже зарегистрирован 1 участник — в нашем примере это main
 * main + Thread-1 + Thread-2 + Thread-3 = 4 участника
 * <p>
 * потом ждет все остальных! phase 1: working
 *
 * <p>
 * register() — добавить участника
 * arriveAndAwaitAdvance() — дойти до фазы и ждать остальных
 * arriveAndDeregister() — дойти и выйти из участников
 * getPhase() — узнать номер текущей фазы
 * <p>
 * arrive()              → я пришёл, но не жду
 * awaitAdvance(phase)   → теперь жду перехода фазы
 * arriveAndAwaitAdvance() → я пришёл и сразу жду
 */

public class PhaserEx {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(1);

        Runnable task = () -> {
            phaser.register();

            try {
                System.out.println(Thread.currentThread().getName() + " phase 0: preparing");
                phaser.arriveAndAwaitAdvance();

                System.out.println(Thread.currentThread().getName() + " phase 1: working");
                phaser.arriveAndAwaitAdvance();

                System.out.println(Thread.currentThread().getName() + " phase 2: finishing");
                phaser.arriveAndAwaitAdvance();

                System.out.println(Thread.currentThread().getName() + " done");
            } finally {
                phaser.arriveAndDeregister();
            }
        };

        new Thread(task, "Thread-1").start();
        new Thread(task, "Thread-2").start();
        new Thread(task, "Thread-3").start();

        System.out.println("main waits for phase 0");
        phaser.arriveAndAwaitAdvance();

        System.out.println("main waits for phase 1");
        phaser.arriveAndAwaitAdvance();

        System.out.println("main waits for phase 2");
        phaser.arriveAndAwaitAdvance();

        System.out.println("main done");
        phaser.arriveAndDeregister();
    }
}