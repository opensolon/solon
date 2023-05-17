package org.noear.solon.admin.server.controller;

import org.noear.solon.admin.server.data.Application;
import org.noear.solon.admin.server.services.ApplicationService;
import org.noear.solon.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

    @Get
    @Mapping("/?")
    public Application getByNameAndBaseUrl(@Param("name") String name, @Param("baseUrl") String baseUrl) throws UnsupportedEncodingException {
        name = URLDecoder.decode(name, "UTF-8");
        baseUrl = URLDecoder.decode(baseUrl, "UTF-8");

        return applicationService.getApplication(name, baseUrl);
    }
}
