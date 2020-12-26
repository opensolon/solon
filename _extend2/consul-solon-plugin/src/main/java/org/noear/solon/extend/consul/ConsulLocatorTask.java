package org.noear.solon.extend.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Service;
import org.noear.solon.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;


/**
 * 负载器获取负载
 *
 * @author 夜の孤城
 * @since 1.2
 */
class ConsulLocatorTask extends TimerTask {
    ConsulClient client;
    LoadBalanceSimpleFactory factory;

    public ConsulLocatorTask(ConsulClient client, LoadBalanceSimpleFactory factory) {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public void run() {
        Map<String, LoadBalanceSimple> tmp = new HashMap<>();
        Response<Map<String, Service>> services = client.getAgentServices();


        for (Map.Entry<String, Service> kv : services.getValue().entrySet()) {
            Service service = kv.getValue();

            if (Utils.isEmpty(service.getAddress())) {
                continue;
            }

            String name = service.getService();

            LoadBalanceSimple loadBalance = tmp.get(name);

            if (loadBalance == null) {
                loadBalance = new LoadBalanceSimple();
                tmp.put(name, loadBalance);
            }

            loadBalance.addServer("http://" + service.getAddress() + ":" + service.getPort());
        }

        factory.update(tmp);
    }
}
