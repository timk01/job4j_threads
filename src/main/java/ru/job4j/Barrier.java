package ru.job4j;

/**
 * здесь Офф и Он - переключатели.
 * (убрал notifyAll(); с оффа, т.к. смсысла в этом нет, т.к. потоки в check() будут разбужены,
 * мотнутся по второму витку цикла т.кк. flag = false и - уйдутв  спячку снова)
 *
 * в-общем это 2 перключателя - см. MultiUser
 *
 * ВАЖНО: while (!flag) - вниманиее, тт должен быть именно цикл
 * (чтобы избежать прблем с согласованностью данных + ложного пробуждеия)
 */

public class Barrier {
    private boolean flag = false;

    private final Object monitor = this;

    public void on() {
        synchronized (monitor) {
            flag = true;
            notifyAll();
        }
    }

    public void off() {
        synchronized (monitor) {
            flag = false;
        }
    }

    public void check() {
        synchronized (monitor) {
            while (!flag) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
