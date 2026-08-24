package org.apache.logging.log4j.solon.integration;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Arrays;

public class DebugDeleteTest {
    @Test
    public void directBasePath() throws Exception {
        File dir = java.nio.file.Files.createTempDirectory("dbg2").toFile();
        String name = new File(dir, "app").getPath();
        ConfigurationBuilder<BuiltConfiguration> b = ConfigurationBuilderFactory.newConfigurationBuilder();
        b.setStatusLevel(org.apache.logging.log4j.Level.DEBUG);
        AppenderComponentBuilder fa = b.newAppender("File", "RollingFile")
                .addAttribute("fileName", name + ".log")
                .addAttribute("filePattern", name + "_%d{yyyy-MM-dd}_%i.log");
        fa.add(b.newLayout("PatternLayout").addAttribute("pattern", "%msg%n"));
        fa.addComponent(b.newComponent("Policies")
                .addComponent(b.newComponent("TimeBasedTriggeringPolicy"))
                .addComponent(b.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "1KB")));
        ComponentBuilder<?> rs = b.newComponent("DefaultRolloverStrategy").addAttribute("max", "7");
        rs.addComponent(b.newComponent("Delete")
                .addAttribute("basePath", dir.getPath())
                .addAttribute("maxDepth", "1")
                .addComponent(b.newComponent("IfFileName").addAttribute("glob", "app_*.log"))
                .addComponent(b.newComponent("IfAccumulatedFileSize").addAttribute("exceeds", "3KB")));
        fa.addComponent(rs);
        b.add(fa);
        b.add(b.newRootLogger("INFO").add(b.newAppenderRef("File")));
        LoggerContext ctx = new LoggerContext("dbg");
        ctx.start(b.build());
        org.apache.logging.log4j.Logger log = ctx.getLogger("t");
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<1100;i++) sb.append('x');
        for (int i=0;i<30;i++) log.info("[{}] {}", i, sb);
        for (int i=0;i<80;i++) { Thread.sleep(200);
            String[] fs = dir.list((d,n)->n.startsWith("app_"));
            System.out.println("poll " + i + " archives=" + (fs==null?-1:fs.length) + " " + Arrays.toString(fs));
            if (fs!=null && fs.length<=3) break;
        }
        ctx.stop();
        for (File f: dir.listFiles()) f.delete();
        dir.delete();
    }
}
