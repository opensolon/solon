/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
