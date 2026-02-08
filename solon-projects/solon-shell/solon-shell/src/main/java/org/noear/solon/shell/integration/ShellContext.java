package org.noear.solon.shell.integration;

import org.noear.solon.shell.core.CommandMetadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShellContext {

    protected static final Map<String, CommandMetadata> COMMAND_REPOSITORY = new HashMap<>();

    // 默认使用中文 Locale（也可通过 Context 获取请求上下文的 Locale，按需调整）
    public static Locale GLOBAL_LOCALE = Locale.SIMPLIFIED_CHINESE;

    // 提供切换 Locale 的方法（供命令调用）
    public static void setGlobalLocale(Locale locale) {
        if (locale != null) {
            GLOBAL_LOCALE = locale;
        }
    }

    public static Map<String, CommandMetadata> getCommandRepository() {
        return Collections.unmodifiableMap(COMMAND_REPOSITORY);
    }
}
