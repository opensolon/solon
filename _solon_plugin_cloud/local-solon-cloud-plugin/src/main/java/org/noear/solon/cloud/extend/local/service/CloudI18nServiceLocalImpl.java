package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.model.Pack;
import org.noear.solon.cloud.service.CloudI18nService;
import org.noear.solon.core.Props;

import java.util.Locale;

/**
 * @author noear
 * @since 1.10
 */
public class CloudI18nServiceLocalImpl implements CloudI18nService {
    @Override
    public Pack pull(String group, String packName, Locale locale) {
        Pack tmp = new Pack(locale);
        tmp.setData(new Props());

        return tmp;
    }
}
