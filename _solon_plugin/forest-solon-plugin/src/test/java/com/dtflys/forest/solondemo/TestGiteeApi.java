package com.dtflys.forest.solondemo;

import com.dtflys.forest.annotation.*;

/**
 * @BaseRequest（baseURL ="https://www.gitee.com"）  直接使用值
 * @BaseRequest（baseURL ="#{test.gitee}"）  使用配置文件中的参数
 * @BaseRequest(baseURL = "upstream://test") 使用配置的upstream
 */
@ForestClient
//@BaseRequest(baseURL = "upstream://gitee")
public interface TestGiteeApi {
    @Get(url="upstream://gitee")
    String search(@Query("q") String q);
}
