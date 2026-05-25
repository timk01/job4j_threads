package ru.job4j.concurrent.atomic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Cache2 {
    private final ConcurrentHashMap<Integer, String> dictionary = new ConcurrentHashMap<>();
    private final AtomicInteger ids = new AtomicInteger();

    public Cache2() {
        dictionary.put(ids.incrementAndGet(), "Petr Arsentev");
        dictionary.put(ids.incrementAndGet(), "Ivan Ivanov");
    }

    public void add(String name) {
        dictionary.put(ids.incrementAndGet(), name);
    }

    public boolean contains(String name) {
        return dictionary.containsValue(name);
    }
}
