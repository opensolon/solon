package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.local.impl.CloudLocalUtils;
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
    static final String I18N_KEY_FORMAT = "i18n/%s_%s-%s";

    private final String server;
    private String packNameDefault;

    public CloudI18nServiceLocalImpl(CloudProps cloudProps) {
        this.server = cloudProps.getServer();

        packNameDefault = cloudProps.getI18nDefault();

        if (Utils.isEmpty(packNameDefault)) {
            packNameDefault = Solon.cfg().appName();
        }

        if (Utils.isEmpty(packNameDefault)) {
            //不能用日志服务（可能会死循环）
            System.err.println("[WARN] Solon.cloud no default i18n is configured");
        }
    }

    @Override
    public Pack pull(String group, String packName, Locale locale) {
        if(Utils.isEmpty(packName)){
            packName = packNameDefault;
        }

        if (Utils.isEmpty(group)) {
            group = Solon.cfg().appGroup();

            if (Utils.isEmpty(group)) {
                group = DEFAULT_GROUP;
            }
        }


        String i18nKey;
        Properties tmp;


        Pack pack = new Pack(locale);
        pack.setData(new Props());

        i18nKey = String.format(I18N_KEY_FORMAT, group, packName, locale.getLanguage());
        tmp = getI18nProps(i18nKey);

        if (tmp != null) {
            pack.getData().putAll(tmp);
        }

        i18nKey = String.format(I18N_KEY_FORMAT, group, packName, locale);
        tmp = getI18nProps(i18nKey);

        if (tmp != null) {
            pack.getData().putAll(tmp);
        }

        return pack;
    }

    private Properties getI18nProps(String i18nKey) {
        try {
            String value2 = CloudLocalUtils.getValue(server, i18nKey);

            if (Utils.isEmpty(value2)) {
                return null;
            } else {
                return Utils.buildProperties(value2);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
