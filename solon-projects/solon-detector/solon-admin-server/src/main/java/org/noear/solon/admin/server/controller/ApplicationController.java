package org.noear.solon.admin.server.controller;

import org.noear.solon.admin.server.data.Application;
import org.noear.solon.admin.server.services.ApplicationService;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Delete;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Post;
import org.noear.solon.annotation.Put;
import org.noear.solon.core.handle.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 应用程序相关 Controller
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Controller
@Mapping("/solon-admin/api/application")
public class ApplicationController {

    @Inject
    private ApplicationService applicationService;

    /**
     * 注册应用程序
     *
     * @param application 应用程序
     */
    @Put
    @Mapping("/register")
    public Result register(@Body Application application) {
        applicationService.registerApplication(application);
        return Result.succeed();
    }

    /**
     * 注销应用程序
     *
     * @param application 应用程序
     */
    @Delete
    @Mapping("/unregister")
    public Result unregister(@Body Application application) {
        applicationService.unregisterApplication(application);
        return Result.succeed();
    }

    /**
     * 发送一次心跳
     *
     * @param application 应用程序
     */
    @Post
    @Mapping("/heartbeat")
    public Result heartbeat(@Body Application application) {
        applicationService.heartbeatApplication(application);
        return Result.succeed();
    }

    /**
     * 获取应用程序信息
     *
     * @param name    应用程序名称
     * @param baseUrl 应用程序 baseUrl
     * @return 应用程序信息
     */
    @Get
    @Mapping("/?")
    public Application getByNameAndBaseUrl(@Param("name") String name, @Param("baseUrl") String baseUrl) throws UnsupportedEncodingException {
        name = URLDecoder.decode(name, "UTF-8");
        baseUrl = URLDecoder.decode(baseUrl, "UTF-8");

        return applicationService.getApplication(name, baseUrl);
    }
}
