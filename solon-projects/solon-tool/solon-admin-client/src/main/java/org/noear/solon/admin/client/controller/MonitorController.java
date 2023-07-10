package org.noear.solon.admin.client.controller;

import org.noear.solon.admin.client.data.Detector;
import org.noear.solon.admin.client.services.ApplicationRegistrationService;
import org.noear.solon.admin.client.services.MonitorService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.util.Collection;

@Controller
@Mapping("/api/monitor")
public class MonitorController {

    @Inject
    private MonitorService monitorService;

    @Inject
    private ApplicationRegistrationService applicationRegistrationService;

    @Get
    @Mapping("/all")
    public Collection<Detector> register() {
        return monitorService.getMonitors();
    }
}
