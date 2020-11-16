package org.noear.fairy.integration.solon;

import org.noear.fairy.Fairy;
import org.noear.fairy.FairyConfigurationDefault;
import org.noear.fairy.FairyException;
import org.noear.fairy.annotation.EnableFairyClient;
import org.noear.fairy.annotation.FairyClient;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XPluginImp implements Plugin {
    private Map<FairyClient, Object> cached = new ConcurrentHashMap<>();

    @Override
    public void start(Solon app) {
        if (app.source().getAnnotation(EnableFairyClient.class) == null) {
            return;
        }

        if(FairyConfigurationDefault.proxy == null) {
            FairyConfigurationDefault.proxy = new FairyConfigurationSolon();
        }

        Aop.context().beanInjectorAdd(FairyClient.class, (varH, anno) -> {
            if (varH.getType().isInterface() == false) {
                return;
            }

            if (Utils.isEmpty(anno.value())) {
                FairyClient anno2 = varH.getType().getAnnotation(FairyClient.class);
                if (anno2 != null) {
                    anno = anno2;
                }
            }

            if (Utils.isEmpty(anno.value())) {
                throw new FairyException("@FairyClient configuration error!");
            }

            Object obj = cached.get(anno);
            if (obj == null) {
                synchronized (anno) {
                    obj = cached.get(anno);
                    if (obj == null) {
                        obj = Fairy.builder().create(varH.getType(), anno);
                        cached.putIfAbsent(anno, obj);
                    }
                }
            }

            varH.setValue(obj);
        });
    }
}
