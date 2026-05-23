package ru.job4j.concurrent;

/**
 * что без джойна ?
 * заустил прогресс, провалился в цикл (интерраптед = фолс - тру, вошли), распечатал старт, стал ждать 10 секунд
 * вернулся к основному треду - он поспал секунду и решил прервать тред второй, а тот - в слипе; а так как в слипе -
 * - вылетело исключение, мы его перехватили, напеечатали фолс и стейт (скорее всего раннейбл),
 * но т.к. интерраптед у нас сейчас = фолс и мы его не меняли, мы провалимся в бесконечный цикл из старта и слипа...
 * <p>
 * Метод join() позволяет вызывающему потоку ждать поток, у которого этот метод вызывается.
 * но поток мейна и не сможет дождаться внутреннего!, тк мы в бесконечном цикле
 * (без джойна мейн завершится, но мы все еще будем в бесконечном цикле потока progress,
 * с джойном - и мейн не завершится)
 * - т.е. ДА,нужно добавлять: Thread.currentThread().interrupt();
 */

public class ThreadStop2 {
    public static void main(String[] args) throws InterruptedException {
        Thread progress = new Thread(
                () -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            System.out.println("start ...");
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        );
        progress.start();
        Thread.sleep(1000);
        progress.interrupt();
        progress.join();
    }
}
