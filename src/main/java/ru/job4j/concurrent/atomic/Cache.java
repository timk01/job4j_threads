package ru.job4j.concurrent.atomic;

/**
 * оба потока могут одновременно, например сдлать проверку на нулль (прочесть), убедиться что кеш еще не создан
 * и - благополучно создать его (т.е. на выходе у нас будет 2 "свежих" синглтона, хотя должен быть один)
 */

public final class Cache {
    private static Cache cache;

    public static Cache getInstance() {
        if (cache == null) {
            cache = new Cache();
        }
        return cache;
    }
}