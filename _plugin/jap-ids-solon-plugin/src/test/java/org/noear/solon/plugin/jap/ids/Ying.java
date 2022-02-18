package org.noear.solon.plugin.jap.ids;

import com.fujieid.jap.ids.solon.integration.XPluginImpl;
import org.noear.solon.Solon;
import org.noear.solon.SolonBuilder;

/**
 * @author 颖
 */
public class Ying {

    public static void main(String[] args) {
        new SolonBuilder()
                .onAppInitEnd(event -> {
                    new XPluginImpl().start(Solon.global());
                }).start(Ying.class, args);
    }

}
