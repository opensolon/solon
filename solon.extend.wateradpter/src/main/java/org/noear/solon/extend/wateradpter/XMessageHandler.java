package org.noear.solon.extend.wateradpter;

import noear.water.WaterClient;

public interface XMessageHandler {
    boolean handler(WaterClient.MessageModel msg) throws Exception;
}
