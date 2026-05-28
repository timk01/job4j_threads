package ru.job4j;

/**
 * slave в бесконечном цикле доходит до barrier.check()
 * пока flag == false, он ждёт
 * main вызывает on(), ставит flag = true и будит поток
 * после этого slave начинает свободно проходить check() и крутиться дальше (ибо flag = true, в цикл не провалимся)
 * когда main вызывает off(), flag снова становится false
 * на следующем заходе в check() поток снова упрётся в ожидание
 * <p>
 * Slave before check
 * main opens barrier
 * Slave after check
 * ... цикл проваливается в свободное плавание ДО теех пор пока мейн не сделает "main closes abrrier"
 * Slave after check
 * Slave before check
 * Slave after check
 * Slave before check
 * ... (нескколько кругов)
 * а потом речь-таки дходоит до мейн-треда, который делаеет off()
 * main closes barrier
 * off() не останавливает поток мгновенно, а просто закрывает барьер, из-за чего поток застрянет на ближайшем check()
 */
public class MultiUserAdvanced {
    public static void main(String[] args) throws InterruptedException {
        Barrier barrier = new Barrier();

        Thread slave = new Thread(
                () -> {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + " before check");
                        barrier.check();
                        System.out.println(Thread.currentThread().getName() + " after check");
                    }
                },
                "Slave"
        );
        slave.start();

        Thread.sleep(1000);
        System.out.println("main opens barrier");
        barrier.on();
        Thread.sleep(1000);
        System.out.println("main closes barrier");
        barrier.off();
    }
}