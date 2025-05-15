package i18n;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.i18n.impl.LocaleResolverCookie;
import org.noear.solon.i18n.impl.LocaleResolverHeader;
import org.noear.solon.i18n.impl.LocaleResolverSession;

import java.util.Locale;

/**
 * @author noear 2025/5/15 created
 */
public class LocaleResolverTest {
    @Test
    public void case11() {
        LocaleResolverHeader localeResolverHeader = new LocaleResolverHeader();
        localeResolverHeader.setHeaderName("lang");

        Locale locale = localeResolverHeader.getLocale(new ContextEmpty() {
            @Override
            public String header(String name) {
                if ("lang".equals(name)) {
                    return "zh_CN";
                } else {
                    return null;
                }
            }
        });

        assert locale != null;
        assert "zh_CN".equals(locale.toString());
    }

    @Test
    public void case12() {
        LocaleResolverHeader localeResolverHeader = new LocaleResolverHeader();

        Locale locale = localeResolverHeader.getLocale(new ContextEmpty() {
            @Override
            public String header(String name) {
                if ("Content-Language".equals(name)) {
                    return "zh_CN";
                } else {
                    return null;
                }
            }
        });

        assert locale != null;
        assert "zh_CN".equals(locale.toString());
    }

    @Test
    public void case13() {
        LocaleResolverHeader localeResolverHeader = new LocaleResolverHeader();

        Locale locale = localeResolverHeader.getLocale(new ContextEmpty() {
            @Override
            public String header(String name) {
                if ("Accept-Language".equals(name)) {
                    return "zh_CN";
                } else {
                    return null;
                }
            }
        });

        assert locale != null;
        assert "zh_CN".equals(locale.toString());
    }

    @Test
    public void case21() {
        LocaleResolverCookie localeResolverCookie = new LocaleResolverCookie();
        localeResolverCookie.setCookieName("lang");

        Locale locale = localeResolverCookie.getLocale(new ContextEmpty() {
            @Override
            public String cookie(String name) {
                if ("lang".equals(name)) {
                    return "zh_CN";
                } else {
                    return null;
                }
            }
        });

        assert locale != null;
        assert "zh_CN".equals(locale.toString());
    }

    @Test
    public void case31() {
        LocaleResolverSession localeResolverSession = new LocaleResolverSession();
        localeResolverSession.setAttrName("lang");

        Locale locale = localeResolverSession.getLocale(new ContextEmpty() {
            @Override
            public <T> T sessionOrDefault(String name, T defaultValue) {
                if ("lang".equals(name)) {
                    return (T) "zh_CN";
                } else {
                    return defaultValue;
                }
            }
        });

        assert locale != null;
        assert "zh_CN".equals(locale.toString());
    }
}