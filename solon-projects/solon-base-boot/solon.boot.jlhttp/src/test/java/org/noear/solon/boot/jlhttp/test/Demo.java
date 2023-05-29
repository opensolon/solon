package org.noear.solon.boot.jlhttp.test;

import org.noear.solon.Solon;

/**
 * @author noear 2022/12/8 created
 */
public class Demo {
    public static void main(String[] args) {
        Solon.start(Demo.class, args, app -> {
            app.http("/", c -> c.output(c.method() + "::hello world!"));
            app.post("/post", c -> c.output(c.method() + "::hello world!"));
        });
    }
}
