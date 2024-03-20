package org.noear.nami.integration.solon;

import org.noear.nami.*;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.common.InfoUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
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
    public void start(AppContext context) {
        if (NamiConfigurationDefault.proxy == null) {
            NamiConfigurationDefault.proxy = new NamiConfigurationSolon(context);
        }

        context.beanInjectorAdd(NamiClient.class, (varH, anno) -> {
            if (varH.getType().isInterface() == false) {
                return;
            }

            boolean localFirst = anno.localFirst();

            if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name())) {
                NamiClient anno2 = varH.getType().getAnnotation(NamiClient.class);
                if (anno2 != null) {
                    anno = anno2;
                }
            }

            localFirst |= anno.localFirst();

            if(localFirst){
                //如果本地优化，开始找 Bean；如果找到就替换注入目标
                context.getBeanAsync(varH.getType(), bean->{
                    varH.setValue(bean);
                });

                if(varH.isDone()){
                    //如果已注入完成
                    return;
                }
            }


            //代理一下，把 name 改掉
            anno = new NamiClientAnno(anno);

            if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name()) && anno.upstream().length == 0) {
                throw new NamiException("@NamiClient configuration error: " + varH.getFullName());
            } else {
                InfoUtils.print(varH.getType(), anno);
            }

            Object obj = cached.get(anno);
            if (obj == null) {
                synchronized (anno) {
                    obj = cached.get(anno);
                    if (obj == null) {
                        obj = Nami.builder().create(varH.getType(), anno);
                        cached.put(anno, obj);
                    }
                }
            }

            varH.setValue(obj);
        });
    }
}
