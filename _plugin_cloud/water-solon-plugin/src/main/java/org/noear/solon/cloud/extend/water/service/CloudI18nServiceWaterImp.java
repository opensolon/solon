package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.model.Pack;
import org.noear.solon.cloud.model.PackHolder;
import org.noear.solon.cloud.service.CloudI18nService;
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
    Map<String, PackHolder> packHolderMap = new ConcurrentHashMap<>();

    @Override
    public Pack pull(String group, String bundleName, Locale locale) {
        try {
            return pullDo(group, bundleName, locale).getPack();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PackHolder pullDo(String group, String packName, Locale locale) throws IOException {
        String packKey = String.format("%s:%s:%s", group, packName, locale.toString());

        PackHolder packHolder = packHolderMap.get(packKey);

        if (packHolder == null) {
            synchronized (packKey.intern()) {
                packHolder = packHolderMap.get(packKey);

                if (packHolder == null) {
                    packHolder = new PackHolder(group, packName, locale);
                    Map<String, String> data = WaterClient.I18n.getI18n(group, packName, packHolder.getLang());
                    packHolder.getPack().setData(data);
                }

                packHolderMap.put(packKey, packHolder);
            }
        }

        return packHolder;
    }

    public void onUpdate(String group, String packName, String lang) {
        String packKey = String.format("%s:%s:%s", group, packName, lang);

        PackHolder packHolder = packHolderMap.get(packKey);

        if (packHolder != null) {
            try {
                Map<String, String> data = WaterClient.I18n.getI18nNoCache(group, packName, packHolder.getLang());
                packHolder.getPack().setData(data);
            } catch (Throwable e) {
                EventBus.push(e);
            }
        }
    }
}
