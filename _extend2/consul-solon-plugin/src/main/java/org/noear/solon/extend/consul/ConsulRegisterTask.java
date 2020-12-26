package org.noear.solon.extend.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;

import java.util.Arrays;

/**
 * 注册任务
 *
 * @author 夜の孤城
 * @since 1.2
 */
class ConsulRegisterTask implements Runnable {
    ConsulClient client;

    public ConsulRegisterTask(ConsulClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        SolonApp app = Solon.global();

        NewService newService = new NewService();

        newService.setPort(app.port());

        String discovery_address = app.cfg().get(Constants.DISCOVERY_ADDRESS);
        if (Utils.isEmpty(discovery_address)) {
            discovery_address = LocalUtils.getLocalAddress();
        }

        newService.setId(app.cfg().appName() + "-" + app.port());
        newService.setName(app.cfg().appName());
        newService.setTags(Arrays.asList("solon", app.cfg().appGroup()));
        newService.setAddress(discovery_address);

        String interval = app.cfg().get(Constants.DISCOVERY_INTERVAL, "10s");

        if (Utils.isNotEmpty(interval)) {
            //1.添加Solon服务，提供检测用
            //
            String path = app.cfg().get(Constants.DISCOVERY_CHECK_PATH, "/actuator/health");
            app.get(path, ctx -> {
                ctx.output("OK");
            });

            //2.添加检测器
            //
            // discovery_address="127.0.0.1";
            String checkUrl = "http://" + discovery_address + ":" + app.port();
            if (path.startsWith("/")) {
                checkUrl = checkUrl + path;
            } else {
                checkUrl = checkUrl + "/" + path;
            }
            NewService.Check check = new NewService.Check();
            check.setInterval(interval);
            check.setMethod("GET");
            check.setHttp(checkUrl);
            check.setDeregisterCriticalServiceAfter("60s");
            check.setTimeout("60s");
            newService.setCheck(check);
        }

        //3.注册到consul
        client.agentServiceRegister(newService);

    }
}
