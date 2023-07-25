package org.noear.solon.admin.client.controller;

import org.noear.solon.admin.client.data.Detector;
import org.noear.solon.admin.client.services.ApplicationRegistrationService;
import org.noear.solon.admin.client.services.MonitorService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.Collection;

/**
 * 监视器 Controller，用于分发客户端监视器信息
 */
@Controller
@Mapping("/api/monitor")
public class MonitorController {

    @Inject
    private MonitorService monitorService;

    @Inject
    private ApplicationRegistrationService applicationRegistrationService;

    /**
     * 获取所有监视器信息
     * @return 所有监视器信息
     */
    @Get
    @Mapping("/all")
    public Collection<Detector> register() {
        return monitorService.getMonitors();
    }
}
