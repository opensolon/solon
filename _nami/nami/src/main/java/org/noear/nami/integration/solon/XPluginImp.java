package org.noear.nami.integration.solon;

import org.noear.nami.*;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.common.InfoUtils;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.2
 * */
public class XPluginImp implements Plugin {
    private Map<NamiClient, Object> cached = new LinkedHashMap<>();

    @Override
    public void start(SolonApp app) {
        if (NamiConfigurationDefault.proxy == null) {
            NamiConfigurationDefault.proxy = new NamiConfigurationSolon();
        }

        Aop.context().beanInjectorAdd(NamiClient.class, (varH, anno) -> {
            if (varH.getType().isInterface() == false) {
                return;
            }

            if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name())) {
                NamiClient anno2 = varH.getType().getAnnotation(NamiClient.class);
                if (anno2 != null) {
                    anno = anno2;
                }
            }

            if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name()) && anno.upstream().length == 0) {
                throw new NamiException("@NamiClient configuration error!");
            } else {
                InfoUtils.print(varH.getType(), anno);
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
