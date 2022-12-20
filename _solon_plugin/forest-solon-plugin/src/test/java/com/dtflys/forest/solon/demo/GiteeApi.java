package com.dtflys.forest.solon.demo;

import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;

/**
 * @BaseRequest（baseURL ="https://www.gitee.com"）  直接使用值
 * @BaseRequest（baseURL ="#{test.gitee}"）  使用配置文件中的参数
 * @BaseRequest(baseURL = "upstream://test") 使用配置的upstream
 */
@ForestClient
public interface GiteeApi {
    @Get(url="upstream://gitee")
    String search(@Query("q") String q);
}
