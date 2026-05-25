package ru.job4j.ref;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * как было, если просто добавлять юзеера:
 * users.put(id.incrementAndGet(), user);
 * мапа безопасна как структура (на то она и конкаррент), но юзер - н безопасен, т.к. мы передаем ему юзера, к которому
 * имеют доступ все (т.е. тот кто последний, будет перезатирать юзера) - это первый момент
 * второй - айдишник тут меняется, а в юзере - нет
 * <p>
 * и именно из-за 1 нужно думать как передавать КОПИЮ объеекта, даже в потокобезопасную коллекцию!
 * <p>
 * defensive copy:
 * users.put(id.incrementAndGet(), User.of(user.getName())); - кладем КОПИЮ (любые извменения извне уже не важны)
 * return User.of(users.get(id).getName()) - позвращаем же копию - здсь логика:
 * в том плане, что мы можем взять юзера по айди иначе и например засеттить новое имя
 *
 * т.е.: 1. новый список (защищенный от конкаррента) 2.
 * в этот момент времени мапа пустая или уже содержит каких-то юзеров,
 * но напрямую их (как список вельюз) отдавать нельзя. решение:
 * пройтись по этим значениям и для каждого юзера создать его ПОЛНУЮ копию
 * (полная копия включает айдишник и коль скоро мнее надо создавать пользователя со всеми потрохами, а айди - недоступен
 * в самом пользователе в плане создания, приходится делать промежуточный метод)
 */

public class UserCache {
    private final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger();

    public void add(User user) {
        users.put(id.incrementAndGet(), User.of(user.getName()));
    }

    public User findById(int id) {
        return User.of(users.get(id).getName());
    }

    public List<User> findAll() {
        return new CopyOnWriteArrayList<>(
                users.values().stream()
                        .map((user) -> User.copyOf(user))
                        .collect(Collectors.toList())
        );
    }
}