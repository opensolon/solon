package org.noear.solon.extend.hotdev;

import org.noear.solon.Solon;

/**
 * 热开发代理，用于操控Solon的启停
 *
 * 这里其实可以直接用Solon,但是Solon的stop在线程里面，重新启动实例时如果端口还在占用，就会重启失败
 *
 * @author 夜の孤城
 * @since 1.5
 */
public class HotdevProxy {

    public static void start(String source, String[] args) throws ClassNotFoundException {
        Solon.start(Class.forName(source), args);
    }

    public static void stop() {
        Solon.cfg().plugs().forEach(p -> p.prestop());
        Solon.cfg().plugs().forEach(p -> p.stop());
    }
}
