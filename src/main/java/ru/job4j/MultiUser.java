package ru.job4j;

/**
 * здесь создается брарьер как класс с методами синхронизации
 * создается 2 треда. мастер - освобождает монитор, включет рубильник
 * слейв - следит за состоянием рубильника в состоянии вейт
 * (но пример слабоват)
 *
 * Пока нить master не выполнит метод on, нить slave не начнет работу. - т.е. так
 */
public class MultiUser {
    public static void main(String[] args) {
        Barrier barrier = new Barrier();
        Thread master = new Thread(
                () -> {
                    System.out.println(Thread.currentThread().getName() + " started");
                    barrier.on();
                },
                "Master"
        );
        Thread slave = new Thread(
                () -> {
                    barrier.check();
                    System.out.println(Thread.currentThread().getName() + " started");
                },
                "Slave"
        );
        master.start();
        slave.start();
    }
}