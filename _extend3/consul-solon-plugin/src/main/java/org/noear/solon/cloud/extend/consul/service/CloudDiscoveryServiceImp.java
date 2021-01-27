package org.noear.solon.cloud.extend.consul.service;

import com.ecwid.consul.json.GsonFactory;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Service;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryObserverEntity;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.solon.cloud.extend.consul.ConsulProps;
import org.noear.solon.cloud.extend.consul.detector.*;
import org.noear.solon.cloud.utils.IntervalUtils;
import org.noear.solon.core.event.EventBus;

import java.util.*;

/**
 * 云端注册与发现服务实现
 *
 * @author 夜の孤城
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryServiceImp extends TimerTask implements CloudDiscoveryService {
    private ConsulClient real;
    private String token;

    private long refreshInterval;

    private String healthCheckInterval;
    private String healthCheckPath;

    Map<String,Discovery> discoveryMap = new HashMap<>();
    private Map<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> observerMap = new HashMap<>();

    /**
     * 初始化客户端
     */
    private void initClient() {
        String server = ConsulProps.instance.getDiscoveryServer();
        String[] ss = server.split(":");

        if (ss.length == 1) {
            real = new ConsulClient(ss[0]);
        } else {
            real = new ConsulClient(ss[0], Integer.parseInt(ss[1]));
        }
    }

    public CloudDiscoveryServiceImp() {
        token = ConsulProps.instance.getToken();
        refreshInterval = IntervalUtils.getInterval(ConsulProps.instance.getDiscoveryRefreshInterval("5s"));

        healthCheckInterval = ConsulProps.instance.getDiscoveryHealthCheckInterval("5s");
        healthCheckPath = ConsulProps.instance.getDiscoveryHealthCheckPath();


        initClient();
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * 注册服务实例
     * */
    @Override
    public void register(String group, Instance instance) {
        String[] ss = instance.address().split(":");
        String serviceId = instance.service() + "-" + instance.address();

        NewService newService = new NewService();

        newService.setId(serviceId);
        newService.setName(instance.service());
        newService.setAddress(ss[0]);
        newService.setPort(Integer.parseInt(ss[1]));
        if (instance.tags() != null) {
            newService.setTags(instance.tags());
        }

        registerLocalCheck(instance, newService);

        //
        // 注册服务
        //
        real.agentServiceRegister(newService, token);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {

    }

    private void registerLocalCheck(Instance instance, NewService newService){
        if (Utils.isNotEmpty(healthCheckInterval)) {
            //1.添加Solon服务，提供检测用
            //
            HealthDetector detector=new HealthDetector();
            detector.startDetect(Solon.global());

            Solon.global().get(healthCheckPath, ctx -> {
                Map<String,Object> info=new HashMap<>();
                info.put("status","OK");
                info.putAll(detector.getInfo());
                ctx.outputAsJson(GsonFactory.getGson().toJson(info));
            });

            //2.添加检测器
            //
            String checkUrl = "http://" + instance.address();
            if (healthCheckPath.startsWith("/")) {
                checkUrl = checkUrl + healthCheckPath;
            } else {
                checkUrl = checkUrl + "/" + healthCheckPath;
            }

            //3.添加检测
            //
            NewService.Check check = new NewService.Check();
            check.setInterval(healthCheckInterval);
            check.setMethod("GET");
            check.setHttp(checkUrl);
            check.setDeregisterCriticalServiceAfter("30s");
            check.setTimeout("60s");

            newService.setCheck(check);
        }
    }

    /**
     * 注销服务实例
     * */
    @Override
    public void deregister(String group, Instance instance) {
        String serviceId = instance.service() + "-" + instance.address();
        real.agentServiceDeregister(serviceId);
    }

    /**
     * 查询服务实例列表
     * */
    @Override
    public Discovery find(String group, String service) {
        return discoveryMap.get(service);
    }

    /**
     * 关注服务实例列表
     * */
    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        observerMap.put(observer, new CloudDiscoveryObserverEntity(group, service, observer));
    }

    /**
     * 定时任务，刷新服务列表
     * */
    @Override
    public void run() {
        try {
            run0();
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    private void run0() {
        Map<String,Discovery> discoveryTmp = new HashMap<>();
        Response<Map<String, Service>> services = real.getAgentServices();

        for (Map.Entry<String, Service> kv : services.getValue().entrySet()) {
            Service service = kv.getValue();

            if (Utils.isEmpty(service.getAddress())) {
                continue;
            }

            String name = service.getService();
            Discovery discovery = discoveryTmp.get(name);

            if (discovery == null) {
                discovery = new Discovery(service.getService());
                discoveryTmp.put(name, discovery);
            }

            Instance instance = new Instance(service.getService(),
                    service.getAddress() + ":" + service.getPort(),
                    null)
                    .tagsAddAll(service.getTags())
                    .metaPutAll(service.getMeta());

            discovery.instanceAdd(instance);
        }

        discoveryMap = discoveryTmp;

        //通知观察者
        noticeObservers();
    }

    /**
     * 通知观察者
     * */
    private void noticeObservers() {
        for (Map.Entry<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> kv : observerMap.entrySet()) {
            CloudDiscoveryObserverEntity entity = kv.getValue();
            Discovery tmp = discoveryMap.get(entity.service);
            if (tmp != null) {
                entity.handler(tmp);
            }
        }
    }


    /**
     * 健康探测器
     * */
    static class HealthDetector {

        private static final Detector[] allDetectors=new Detector[]{
                new CpuDetector(),
                new JvmMemoryDetector(),
                new OsDetector(),
                new QpsDetector(),
                new MemoryDetector(),
                new DiskDetector()
        };
        Set<Detector> detectors = new HashSet<>();

        public HealthDetector(){

        }

        public void startDetect(SolonApp app){
            String detectorNamesStr= ConsulProps.instance.getDiscoveryHealthDetector();

            if(Utils.isEmpty(detectorNamesStr)){
                return;
            }

            Set<String> detectorNames=new HashSet<>(Arrays.asList(detectorNamesStr.split(",")));

            for(Detector detector:allDetectors){
                if(detectorNames.contains(detector.getName())){
                    detectors.add(detector);
                    if(detector instanceof QpsDetector){
                        ((QpsDetector) detector).toDetect(app);
                    }
                }
            }
        }

        public Map<String,Object> getInfo(){
            Map<String,Object> info=new HashMap<>();
            for(Detector detector:detectors){
                info.put(detector.getName(),detector.getInfo());
            }
            return info;
        }
    }
}
