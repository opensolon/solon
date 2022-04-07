package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.service.CloudI18nService;
import org.noear.water.WaterClient;

import java.util.Locale;
import java.util.Map;

/**
 * @author noear
 * @since 1.6
 */
public class CloudI18nServiceWaterImp implements CloudI18nService {
    @Override
    public Map<String, String> pull(String group, String bundleName, Locale locale) {
        try {
            return WaterClient.I18n.getI18n(group, bundleName, locale.toString());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
