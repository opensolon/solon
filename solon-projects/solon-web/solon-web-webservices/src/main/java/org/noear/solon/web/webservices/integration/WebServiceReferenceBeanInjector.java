package org.noear.solon.web.webservices.integration;

import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.web.webservices.WebServiceReference;
import org.noear.solon.web.webservices.WebServiceHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.0
 */
public class WebServiceReferenceBeanInjector implements BeanInjector<WebServiceReference> {
    private Map<String, Object> cached = new ConcurrentHashMap<>();

    @Override
    public void doInject(VarHolder varH, WebServiceReference anno) {
        if (varH.getType().isInterface()) {

            String wsKey = anno.value() + "#" + varH.getType().getTypeName();
            Object wsProxy = cached.computeIfAbsent(wsKey, k -> WebServiceHelper.createWebClient(anno.value(), varH.getType()));

            varH.setValue(wsProxy);
        }
    }
}
