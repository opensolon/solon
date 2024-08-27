package labs;

import org.noear.solon.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Set;
import java.util.TreeSet;

/**
 * 聚合一个项目的 NOTICE
 *
 * @author noear 2024/8/27 created
 */
public class NoticeAggregators {
    public static void main(String[] args) throws Exception {
        File dir = new File("/demo/");
        Set<String> sets = new TreeSet<>();

        build(dir, sets);

        for (String line : sets) {
            if (line.startsWith("  solon") == false) {
                System.out.println(line);
            }
        }
    }

    public static void build(File dir, Set<String> sets) throws Exception {
        if(dir.exists() == false) {
            return;
        }

        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                build(f, sets);
            } else {
                if ("NOTICE".equals(f.getName())) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                        while (reader.ready()) {
                            String line = reader.readLine();
                            if (Utils.isNotEmpty(line)) {
                                sets.add(line);
                            }
                        }
                    }
                }
            }
        }
    }
}