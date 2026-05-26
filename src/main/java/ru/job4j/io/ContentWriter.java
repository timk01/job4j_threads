package ru.job4j.io;

import java.io.*;

public final class ContentWriter {
    private final FileContext fileContext;

    public ContentWriter(FileContext fileContext) {
        this.fileContext = fileContext;
    }

    public void saveContent(String content) throws IOException {
        synchronized (fileContext.getObject()) {
            try (BufferedWriter br = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileContext.getFile())))) {
                for (int i = 0; i < content.length(); i++) {
                    br.write(content.charAt(i));
                }
            }
        }
    }
}