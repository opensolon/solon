package org.noear.solon.web.webservices.integration;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 1.0
 */
public class WebServiceBeanBuilder implements BeanBuilder<WebService> {
    private List<BeanWrap> wsBeanWarps = new ArrayList<>();

    public List<BeanWrap> getWsBeanWarps() {
        return wsBeanWarps;
    }

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, WebService anno) throws Throwable {
        if (clz.isInterface() == false) {
            wsBeanWarps.add(bw);
        }
    }
}
