package org.noear.nami.integration.solon;

import org.noear.nami.Nami;
import org.noear.nami.NamiConfigurationDefault;
import org.noear.nami.NamiException;
import org.noear.nami.annotation.EnableNamiClient;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XPluginImp implements Plugin {
    private Map<NamiClient, Object> cached = new ConcurrentHashMap<>();

    @Override
    public void start(SolonApp app) {
        if (app.source().getAnnotation(EnableNamiClient.class) == null) {
            return;
        }

        if(NamiConfigurationDefault.proxy == null) {
            NamiConfigurationDefault.proxy = new NamiConfigurationSolon();
        }

        Aop.context().beanInjectorAdd(NamiClient.class, (varH, anno) -> {
            if (varH.getType().isInterface() == false) {
                return;
            }

            if (Utils.isEmpty(anno.value())) {
                NamiClient anno2 = varH.getType().getAnnotation(NamiClient.class);
                if (anno2 != null) {
                    anno = anno2;
                }
            }

            if (Utils.isEmpty(anno.value())) {
                throw new NamiException("@FairyClient configuration error!");
            }

            Object obj = cached.get(anno);
            if (obj == null) {
                synchronized (anno) {
                    obj = cached.get(anno);
                    if (obj == null) {
                        obj = Nami.builder().create(varH.getType(), anno);
                        cached.putIfAbsent(anno, obj);
                    }
                }
            }

            varH.setValue(obj);
        });
    }
}
