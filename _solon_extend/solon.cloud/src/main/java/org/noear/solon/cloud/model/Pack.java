package org.noear.solon.cloud.model;

import org.noear.solon.core.Props;

import java.util.Locale;
import java.util.Map;

/**
 * 数据包
 *
 * @author noear
 * @since 1.6
 */
public class Pack {
    private final Locale locale;
    private final String lang;

    private Props data;

    public Pack(Locale locale) {
        this.locale = locale;
        this.lang = locale.toString();
    }

    public void setData(Props data) {
        this.data = data;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLang() {
        return lang;
    }

    public Props getData() {
        return data;
    }
}
