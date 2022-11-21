package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Pack;
import org.noear.solon.cloud.service.CloudI18nService;
import org.noear.solon.core.Props;

import java.util.Locale;
import java.util.Properties;

/**
 * @author noear
 * @since 1.10
 */
public class CloudI18nServiceLocalImpl implements CloudI18nService {
    static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    static final String I18N_KEY_FORMAT = "i18n@%s:%s-%s.properties";

    @Override
    public Pack pull(String group, String packName, Locale locale) {
        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }


        String i18nKey = String.format(I18N_KEY_FORMAT, group, packName, locale.toString());

        Properties i18nProp = Utils.loadProperties(i18nKey);

        Pack tmp = new Pack(locale);
        tmp.setData(new Props());

        if (i18nProp == null) {
            tmp.getData().putAll(i18nProp);
        }

        return tmp;
    }
}
