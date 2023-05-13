package org.noear.solon.admin.server.controller;

import org.noear.solon.admin.server.data.Application;
import org.noear.solon.admin.server.services.ApplicationService;
import org.noear.solon.annotation.*;

import java.util.Set;

@Controller
@Mapping("/api/application")
public class ApplicationController {

    @Inject
    private ApplicationService applicationService;

    @Put
    @Mapping("/register")
    public void register(@Body Application application) {
        applicationService.registerApplication(application);
    }

    @Delete
    @Mapping("/unregister")
    public void unregister(@Body Application application) {
        applicationService.unregisterApplication(application);
    }

    @Get
    @Mapping("/all")
    public Set<Application> all() {
        return applicationService.getApplications();
    }

    @Post
    @Mapping("/heartbeat")
    public void heartbeat(@Body Application application) {
        applicationService.heartbeatApplication(application);
    }
}
