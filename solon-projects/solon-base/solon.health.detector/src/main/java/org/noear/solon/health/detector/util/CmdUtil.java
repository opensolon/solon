package org.noear.solon.health.detector.util;

import org.noear.solon.Utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 命令行执行工具
 *
 * @author noear
 * @since 2.2
 */
public class CmdUtil {

    public static String execute(String... command) throws Exception {
        return execute(false, command);
    }

    public static String execute(boolean firstLine, String... command) throws Exception {
        String text = null;
        InputStream is = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(new String[0]);
            builder.command(command);
            Process process = builder.start();
            process.getOutputStream().close();
            is = process.getInputStream();
            if (firstLine) {
                InputStreamReader isr = new InputStreamReader(is);
                LineNumberReader lnr = new LineNumberReader(isr);
                text = lnr.readLine();
                lnr.close();
                isr.close();
            } else {
                text = Utils.transferToString(is,"utf-8");
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }
}
