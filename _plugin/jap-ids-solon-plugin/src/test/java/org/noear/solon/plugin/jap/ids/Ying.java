package org.noear.solon.plugin.jap.ids;

import org.noear.solon.Solon;
import org.noear.solon.SolonBuilder;
import org.noear.solon.plugin.jap.ids.services.IdsCacheImpl;
import org.noear.solon.plugin.jap.ids.services.IdsClientDetailServiceImpl;

/**
 * @author 颖
 */
public class Ying {

    public static void main(String[] args) {
        new SolonBuilder()
                .onAppInitEnd(event -> {
                    new PluginImpl().start(Solon.global());
                }).start(Ying.class, args);
    }

}
