package org.noear.solon.gradle.util;

import java.io.*;

public class IoUtils {

    public static void writeString(File file, String str) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(str.getBytes());
        }
    }

    public static String readString(File file) throws IOException {
        try (BufferedReader fis = new BufferedReader(new FileReader(file))) {

            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = fis.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        }
    }
}
