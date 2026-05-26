package ru.job4j.io;

import java.io.*;
import java.util.function.Predicate;

public final class ContentReader {
    private final FileContext fileContext;

    public ContentReader(FileContext fileContext) {
        this.fileContext = fileContext;
    }

    /**
     * в первом случае - тру, во втором проверка на аскии - харктеры
     * Predicate<Character> filter2 = (character) -> true;
     * Predicate<Character> filter3 = (character) -> character < 0x80;
     *
     * @param filter
     * @return
     * @throws IOException
     */

    public String getContent(Predicate<Character> filter) throws IOException {
        synchronized (fileContext.getObject()) {
            StringBuilder stringBuilder = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new FileReader(fileContext.getFile()))) {

                int charValue;

                while ((charValue = br.read()) != -1) {
                    char ch = (char) charValue;
                    if (filter.test(ch)) {
                        stringBuilder.append(ch);
                    }
                }
            }
            return stringBuilder.toString();
        }
    }
}