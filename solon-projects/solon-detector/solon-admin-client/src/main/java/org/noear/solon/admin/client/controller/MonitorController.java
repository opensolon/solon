package org.noear.solon.admin.client.controller;

import org.noear.solon.admin.client.config.ClientProperties;
import org.noear.solon.admin.client.data.Detector;
import org.noear.solon.admin.client.services.ApplicationRegistrationService;
import org.noear.solon.admin.client.services.MonitorService;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;

import java.util.Collection;

/**
 * 监视器 Controller，用于分发客户端监视器信息
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Controller
@Mapping("/solon-admin/api/monitor")
public class MonitorController {
    @Inject
    private ClientProperties properties;

    @Inject
    private MonitorService monitorService;

    @Inject
    private ApplicationRegistrationService applicationRegistrationService;

    /**
     * 获取所有监视器信息
     *
     * @return 所有监视器信息
     */
    @Get
    @Mapping("/data")
    public Collection<Detector> data(@Header("TOKEN") String token, Context ctx) {
        if (properties.getToken().equals(token)) {
            return monitorService.getMonitors();
        } else {
            ctx.status(401);
            return null;
        }
    }
}
