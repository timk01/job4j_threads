package ru.job4j.concurrent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;

public class Wget2 implements Runnable {
    private final String url;
    private final int speed;

    public Wget2(String url, int speed) {
        this.url = url;
        this.speed = speed;
    }

    /**
     * есть скорость, например 1000 байт в секунду (топовая).
     * если мы эти 1000 байт скачали не в секунлу, а за 0.5 секунды (здесь у меня 2 прохода),
     * надо будет спать непобходимую дельту
     * long expectedTime = overallBytesRead * 1000L / speed; - как раз то, за сколько предполагатся скачать нечто
     * (накопительный счетчик)
     * passedTimeFromStart - реальное время, оно же постоянно сдвигается после каждого тика цикла
     * <p>
     * если рально сеть очень быстрая (т.е. мы примерно сразу читаем необходимые 512 байт)
     * xpectedTime = 512 * 1000 / 1000 = 512 мс, а реально прошло 5 мс, мы будем спать в этот тик 512 - 5 - почти 0.5 с
     * <p>
     * если сеть меедленная - т.е. либо мы не читаем полный буффер (т.е. expectedTime будет страдать - будет меньше),
     * ЛИБО мы это делаем, но задержки между чтениями большие - медленный диск/потери пекетов
     * (тогда уже passedTimeFromStart страдает - вырастает)
     * может быть картина, что 1300 > 1000 или 1000 > 5000
     * (тогда и "резать скорость" / вставлять паузы нет смысла)
     * Instant startPoint = Instant.now();
     * byte[] dadaBuffer = new byte[512];
     * long passedTimeFromStart;
     * int bytesRead;
     * int overallBytesRead = 0;
     * while ((bytesRead = input.read(dadaBuffer, 0, dadaBuffer.length)) != -1) {
     * overallBytesRead += bytesRead;
     * Instant now = Instant.now();
     * passedTimeFromStart = Duration.between(startPoint, now).toMillis();
     * long expectedTime = overallBytesRead * 1000L / speed;
     * if (expectedTime > passedTimeFromStart) {
     * Thread.sleep(expectedTime - passedTimeFromStart);
     * }
     * }
     * <p>
     * ЭТО - был накопитальный счетчик
     * <p>
     * А вот это - оконный:
     * есть лимит: speed байт в секунду
     * значит окно берём 1 секунду
     * внутри этого окна копим байты
     * как только набрали >= speed байт (т.е. наша сумма байтов больше или равна тех, что нужно было загрузить):
     * смотрим, сколько прошло времени
     * если меньше 1000 мс → значит скачали слишком быстро, можно sleep(1000 - passed)
     * если 1000 мс или больше → значит лимит и так не нарушен, ничего не делаем,
     * тупо ОБРЕЗАЕМ счетчик байтов (чтобы не потерять хвост из данных)
     * + сдвигаем стартпойнт на "нау" - конец текущего окнца как стартпойнт
     * <p>
     * То есть да:
     * <p>
     * прошло меньше секунды → спим
     * прошло больше секунды → просто скипаем паузу
     * <p>
     * totalBytesReadInWindow += bytesRead;
     * <p>
     * if (totalBytesReadInWindow > speed) {
     * passedTimeInWindow = Duration.between(startPoint, Instant.now()).toMillis();
     * <p>
     * if (passedTimeInWindow < 1000) {
     * Thread.sleep(1000 - passedTimeInWindow);
     * }
     * totalBytesReadInWindow = totalBytesReadInWindow - speed;
     * startPoint = Instant.now();
     * }
     * <p>
     * здеесь мы говорим про окно которое равняетс сеекунде (потому 1000)
     * Если speed = 1000 байт/сек, то это значит:
     * <p>
     * за 1 секунду можно скачать 1000 байт
     * если ты скачал 1025 байт за 300 мс — перебрал, надо тормозить
     * если ты скачал 1025 байт за 1400 мс — всё ок, и так медленно, спать не надо
     */
    @Override
    public void run() {
        try (InputStream input = new URL(url).openStream()) {
            Instant startPoint = Instant.now();
            byte[] dadaBuffer = new byte[512];
            long passedTimeInWindow;
            int bytesRead;
            int totalBytesReadInWindow = 0;
            while ((bytesRead = input.read(dadaBuffer, 0, dadaBuffer.length)) != -1) {
                totalBytesReadInWindow += bytesRead;

                if (totalBytesReadInWindow >= speed) {
                    passedTimeInWindow = Duration.between(startPoint, Instant.now()).toMillis();

                    if (passedTimeInWindow < 1000) {
                        Thread.sleep(1000 - passedTimeInWindow);
                    }
                    totalBytesReadInWindow = totalBytesReadInWindow - speed;
                    startPoint = Instant.now();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        validateParams(args);
        String url = args[0];
        int speed = Integer.parseInt(args[1]);
        Thread wget2 = new Thread(new Wget2(url, speed));
        wget2.start();
        wget2.join();
    }

    private static void validateParams(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("params quantity should be 2");
        }

        if (args[0] == null || args[1] == null) {
            throw new IllegalArgumentException("parameters cannot be null");
        }

        if (args[0].isBlank() || args[1].isBlank()) {
            throw new IllegalArgumentException("parameters cannot be empty/blank");
        }

        checkURL(args[0]);

        checkSpeed(args[1]);
    }

    private static void checkURL(String second) {
        try {
            URI uri = new URI(second);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if ((!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new IllegalArgumentException("URL protocol must be http or https");
            }

            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("URL host must not be empty");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL param is not legal");
        }
    }

    private static void checkSpeed(String first) {
        int number;
        try {
            number = Integer.parseInt(first);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("second parameter must be a number");
        }

        if (number <= 0) {
            throw new IllegalArgumentException("second parameter must be positive");
        }
    }
}
