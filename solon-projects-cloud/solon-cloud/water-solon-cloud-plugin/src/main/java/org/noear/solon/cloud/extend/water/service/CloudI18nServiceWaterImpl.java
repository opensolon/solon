package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Pack;
import org.noear.solon.cloud.service.CloudI18nService;
import org.noear.solon.core.Props;
import org.noear.water.WaterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author noear
 * @since 1.6
 */
public class CloudI18nServiceWaterImpl implements CloudI18nService {
    static final Logger log = LoggerFactory.getLogger(CloudI18nServiceWaterImpl.class);

    private String packNameDefault;
    private Map<String, Pack> packMap = new HashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    public CloudI18nServiceWaterImpl(CloudProps cloudProps){
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

        try {
            return pullDo(group, packName, locale);
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
            SYNC_LOCK.lock();
            try {
                pack = packMap.get(packKey);

                if (pack == null) {
                    pack = new Pack(locale);
                    Map<String, String> data = WaterClient.I18n.getI18n(group, packName, pack.getLang());
                    pack.setData(new Props(data));
                }

                packMap.put(packKey, pack);
            } finally {
                SYNC_LOCK.unlock();
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
                log.warn(e.getMessage(), e);
            }
        }
    }
}
