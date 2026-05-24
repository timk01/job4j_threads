package ru.job4j.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class FileDownload {
    public static void main(String[] args) throws IOException {
        long startAt = System.currentTimeMillis();
        File file = new File("tmp.xml");
        String url = "https://raw.githubusercontent.com/peterarsentev/course_test/master/pom.xml";

        try (InputStream input = new URL(url).openStream();
             FileOutputStream output = new FileOutputStream(file);
        ) {
            System.out.println("Open connection: " + (System.currentTimeMillis() - startAt) + " ms");
            byte[] dadaBuffer = new byte[512];
            int bytesRead;
            while ((bytesRead = input.read(dadaBuffer, 0, dadaBuffer.length)) != -1) {
                long downloadAt = System.nanoTime();
                output.write(dadaBuffer, 0, bytesRead);
                System.out.println("Read 512 bytes : " + (System.nanoTime() - downloadAt) + " nano.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Files.size(file.toPath()) + " bytes");
    }
}
