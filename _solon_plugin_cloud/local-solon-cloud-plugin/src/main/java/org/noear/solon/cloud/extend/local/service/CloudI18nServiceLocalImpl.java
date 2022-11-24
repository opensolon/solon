package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Pack;
import org.noear.solon.cloud.service.CloudI18nService;
import org.noear.solon.core.Props;

import java.util.Locale;
import java.util.Properties;

/**
 * 云端国际化（本地摸拟实现）
 *
 * @author noear
 * @since 1.11
 */
public class CloudI18nServiceLocalImpl implements CloudI18nService {
    static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    static final String I18N_KEY_FORMAT = "META-INF/solon-cloud/i18n@%s_%s-%s";

    @Override
    public Pack pull(String group, String packName, Locale locale) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }


        String bundleName;
        Properties tmp;


        Pack pack = new Pack(locale);
        pack.setData(new Props());

        bundleName = String.format(I18N_KEY_FORMAT, group, packName, locale.getLanguage());
        tmp = getProps(bundleName);

        if (tmp != null) {
            pack.getData().putAll(tmp);
        }

        bundleName = String.format(I18N_KEY_FORMAT, group, packName, locale);
        tmp = getProps(bundleName);

        if (tmp != null) {
            pack.getData().putAll(tmp);
        }

        return pack;
    }

    private Properties getProps(String uri) {
        try {
            String txt = Utils.getResourceAsString(uri);

            if (Utils.isEmpty(txt)) {
                return null;
            } else {
                return Utils.buildProperties(txt);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
