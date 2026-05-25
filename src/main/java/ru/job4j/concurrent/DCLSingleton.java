package ru.job4j.concurrent;

/**
 * volatile гарантирует, что каждый раз при ЧТЕНИИ этой переменной поток будет видеть последнее
 * записанное другим потоком значение
 * <p>
 * создание объекта = не томарная ситуация. может произойти случай, когда память уже выделена,
 * ссылка есть, а сам объект - не создан (и т.к. мы можем спросить "А ты нуль" - мы получим НЕ тру, и не создадим его),
 * волатиь в этом плане позвляет обеспечить полное чтение "слепка" объекта (или короче, когда он уже создан)..
 * <p>
 * ЗЫ: а проще просто в синхронайзед обернуть...
 */

public final class DCLSingleton {

    private static volatile DCLSingleton instance;

    private DCLSingleton() {
    }

    public static DCLSingleton getInstance() {
        if (instance == null) {
            synchronized (DCLSingleton.class) {
                if (instance == null) {
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}