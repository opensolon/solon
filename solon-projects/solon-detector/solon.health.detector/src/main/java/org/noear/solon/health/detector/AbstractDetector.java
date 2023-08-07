package org.noear.solon.health.detector;

import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 检测器虚拟类
 *
 * @author noear
 * @since 1.5
 * */
public abstract class AbstractDetector implements Detector {

    protected final static String osName = System.getProperty("os.name").toLowerCase();

    protected List<String[]> matcher(Pattern pattern, String text) throws Exception {
        List<String[]> infos = new ArrayList();
        if (Utils.isNotEmpty(text)) {
            String[] lines = text.trim().split("\\n");
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String[] info = new String[matcher.groupCount() + 1];

                    for (int i = 0; i <= matcher.groupCount(); ++i) {
                        info[i] = matcher.group(i).trim();
                    }
                    infos.add(info);
                }
            }
        }
        return infos;
    }
}
