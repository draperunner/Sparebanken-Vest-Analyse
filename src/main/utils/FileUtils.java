package main.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by mats on 28.07.2015.
 */
public class FileUtils {

    public static String guessEncoding(File file) {
        String defaultCharset = Charset.defaultCharset().toString();
        List<String> encodings = Arrays.asList(defaultCharset, "UTF-8", "windows-1252", "ISO-8859-1");
        for (String encoding : encodings) {
            try {
                if (encodingWorks(file, encoding)) {
                    return encoding;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return defaultCharset;
    }

    private static boolean encodingWorks(File file, String encoding) throws FileNotFoundException {
        Scanner scanner = new Scanner(file, encoding);
        boolean result = scanner.hasNextLine();
        scanner.close();
        return result;
    }
}
