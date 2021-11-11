package org.noear.solon.extend.hotdev;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.ext.ConsumerEx;

/**
 * 热开发 - 启停代理代理（用于操控Solon的启停）
 *
 * @author 夜の孤城
 * @since 1.5
 */
public class HotdevProxy {

    /**
     * 启动服务
     */
    public static void start(String source, String[] args) throws ClassNotFoundException {
        Solon.start(Class.forName(source), args);
    }

    /**
     * 关掉服务
     * */
    public static void stop() {
        Solon.cfg().plugs().forEach(p -> p.prestop());
        Solon.cfg().plugs().forEach(p -> p.stop());
    }
}
