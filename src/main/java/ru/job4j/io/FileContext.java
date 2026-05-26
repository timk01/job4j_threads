package ru.job4j.io;

import java.io.File;

public final class FileContext {
    private final Object object = new Object();
    private final File file;

    public FileContext(File file) {
        this.file = file;
    }

    public Object getObject() {
        return object;
    }

    public File getFile() {
        return file;
    }
}
