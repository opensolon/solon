package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.model.Pack;
import org.noear.solon.cloud.service.CloudI18nService;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.EventBus;
import org.noear.water.WaterClient;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.6
 */
public class CloudI18nServiceWaterImp implements CloudI18nService {
    Map<String, Pack> packMap = new ConcurrentHashMap<>();

    @Override
    public Pack pull(String group, String bundleName, Locale locale) {
        try {
            return pullDo(group, bundleName, locale);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Pack pullDo(String group, String packName, Locale locale) throws IOException {
        String packKey = String.format("%s:%s:%s", group, packName, locale.toString().toLowerCase(Locale.ROOT));

        Pack pack = packMap.get(packKey);

        if (pack == null) {
            synchronized (packKey.intern()) {
                pack = packMap.get(packKey);

                if (pack == null) {
                    pack = new Pack(locale);
                    Map<String, String> data = WaterClient.I18n.getI18n(group, packName, pack.getLang());
                    pack.setData(new Props(data));
                }

                packMap.put(packKey, pack);
            }
        }

        return pack;
    }

    public void onUpdate(String group, String packName, String lang) {
        String packKey = String.format("%s:%s:%s", group, packName, lang.toLowerCase(Locale.ROOT));

        Pack pack = packMap.get(packKey);

        if (pack != null) {
            try {
                Map<String, String> data = WaterClient.I18n.getI18nNoCache(group, packName, pack.getLang());
                pack.setData(new Props(data));
            } catch (Throwable e) {
                EventBus.push(e);
            }
        }
    }
}
