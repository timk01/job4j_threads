package ru.job4j.concurrent;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CountOldTest {

    @Test
    public void whenExecute2ThreadThen2() throws InterruptedException {
        CountOld countOld = new CountOld();
        Thread first = new Thread(countOld::simpleIncrement);
        Thread second = new Thread(countOld::simpleIncrement);

        first.start();
        second.start();
        first.join();
        second.join();

        assertThat(countOld.get()).isEqualTo(2);
    }

}