package lab.expr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author noear 2025/3/15 created
 */
public class ClassAsOne {
    public static void main(String[] args) throws IOException {
        //print("/solon-projects/solon-expression/solon-expression/src/main/java/");
        print("/solon/src/main/java/");
    }

    public static void print(String dir) throws IOException {
        String directoryPath = System.getProperty("user.dir") + dir;

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            System.out.println("// file: " + path.getFileName());
                            String classCode = new String(Files.readAllBytes(path));
                            System.out.println(classCode);
                            System.out.println("\n\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
