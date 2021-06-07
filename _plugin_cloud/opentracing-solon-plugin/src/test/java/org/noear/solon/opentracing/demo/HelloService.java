package org.noear.solon.opentracing.demo;

import org.noear.nami.annotation.NamiClient;

/**
 * @author noear 2021/6/7 created
 */
@NamiClient(name = "hellorpc", path = "rpc")
public interface HelloService {
    String hello(String name);
}
