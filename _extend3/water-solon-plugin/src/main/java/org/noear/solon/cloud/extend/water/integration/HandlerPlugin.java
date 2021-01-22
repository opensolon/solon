package org.noear.solon.cloud.extend.water.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.cloud.extend.water.integration.http.HandlerCheck;
import org.noear.solon.cloud.extend.water.integration.http.HandlerReceive;
import org.noear.solon.cloud.extend.water.integration.http.HandlerStatus;
import org.noear.solon.cloud.extend.water.integration.http.HandlerStop;
import org.noear.solon.cloud.extend.water.service.CloudEventServiceImp;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.core.Plugin;
import org.noear.water.WW;

/**
 * @author noear
 * @since 1.2
 */
public class HandlerPlugin implements Plugin {
    CloudEventServiceImp eventService;
    Instance registryInstance;

    public HandlerPlugin(CloudEventServiceImp eventService, Instance registryInstance) {
        this.eventService = eventService;
        this.registryInstance = registryInstance;
    }

    @Override
    public void start(SolonApp app) {
        app.http(WW.path_run_check, new HandlerCheck());
        app.http(WW.path_run_status, new HandlerStatus());
        app.http(WW.path_run_stop, new HandlerStop(registryInstance));
        app.http(WW.path_msg_receiver, new HandlerReceive(eventService));
    }
}
