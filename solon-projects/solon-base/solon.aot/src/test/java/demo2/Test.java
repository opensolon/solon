package demo2;

import org.noear.solon.Solon;
import org.noear.solon.aot.Settings;
import org.noear.solon.aot.proxy.ProxyClassGenerator;
import org.noear.solon.core.runtime.NativeDetector;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author noear 2023/4/26 created
 */
public class Test {
    public static void main(String[] args) {
        System.setProperty(NativeDetector.AOT_PROCESSING, "1");

        Solon.start(Test.class, args);

        String pathStr = new File("/Users/noear/Downloads/temp3").getPath();
        Path path = Paths.get(pathStr);

        Settings settings = new Settings(path, path, "", "");

        ProxyClassGenerator proxyClassGenerator = new ProxyClassGenerator();
        proxyClassGenerator.generateCode(settings, TagServiceImpl.class);

    }
}
