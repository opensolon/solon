package com.dtflys.forest.solondemo;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.LoadBalance;

/**
 * 定义一个负载器（可以对接发现服务）
 * */
@Component("gitee")
public class GiteeUpstream implements LoadBalance {
    @Override
    public String getServer() {
        return "https://www.gitee.com";
    }
}
