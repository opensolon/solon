package org.noear.solon.cloud.extend.polaris.service;

import com.tencent.polaris.configuration.api.core.*;
import com.tencent.polaris.configuration.factory.ConfigFileServiceFactory;
import com.tencent.polaris.factory.ConfigAPIFactory;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Config;
import org.noear.solon.cloud.service.CloudConfigObserverEntity;
import org.noear.solon.cloud.service.CloudConfigService;

import java.util.*;

public class CloudConfigServicePolarisImp implements CloudConfigService {

    private Map<CloudConfigHandler, CloudConfigObserverEntity> observerMap = new HashMap<>();
    private ConfigFileService configFileService;
    private String namespace ;
    private String fileName ;


    public CloudConfigServicePolarisImp(CloudProps cloudProps) {


        String server = cloudProps.getServer();
        namespace = cloudProps.getProp("global").get("namespace", Solon.cfg().appNamespace());
        fileName = cloudProps.getProp("global").getProp("config").get("file");
        String address =  cloudProps.getProp("global").getProp("config").get("address", namespace);

        ConfigurationImpl configuration = (ConfigurationImpl)ConfigAPIFactory.defaultConfig();
        configuration.getGlobal().getSystem().getConfigCluster().setNamespace(namespace);
        configuration.getGlobal().getSystem().getConfigCluster().setService(server);
        configuration.getConfigFile().getServerConnector().setAddresses(Arrays.asList(address));

        this.configFileService = ConfigFileServiceFactory.createConfigFileService(configuration);

    }

    /**
     * 拉取配置
     *
     * @param group 分组
     * @param name  配置名
     * @return
     */
    @Override
    public Config pull(String group, String name) {
        group = Utils.isEmpty(group) ? Solon.cfg().appGroup() : group;

        String value = configFileService.getConfigYamlFile(namespace, group, fileName).getProperty(name, "");
        // System.out.println(String.format(" 北极星配置拉取 group:[%s],fileName:[%s],name:[%s],value:[%s]", group,fileName,name,value));
        return new Config(group, name, value, 0);
    }

    /**
     * 设置配置
     *
     * @param group 分组
     * @param name  配置名
     * @param value 值
     * @return
     */
    @Override
    public boolean push(String group, String name, String value) {
        throw new RuntimeException("暂未实现");
    }

    /**
     * @param group 分组
     * @param name  配置名
     * @return
     */
    @Override
    public boolean remove(String group, String name) {
        throw new RuntimeException("暂未实现");
    }

    /**
     * 监听配置的修改
     *
     * @param group    分组
     * @param name     配置名
     * @param observer 观察者
     */
    @Override
    public void attention(String group, String name, CloudConfigHandler observer) {
        if (observerMap.containsKey(observer)) {
            return;
        }

        group = Utils.isEmpty(group) ? Solon.cfg().appGroup() : group;

        CloudConfigObserverEntity entity = new CloudConfigObserverEntity(group, name, observer);
        observerMap.put(observer, entity);

        ConfigKVFile configFile = configFileService.getConfigYamlFile(namespace, group, fileName);

        configFile.addChangeListener((ConfigKVFileChangeListener) event -> {
            Set<String> changedKeys = event.changedKeys();
            if (Objects.nonNull(changedKeys) && changedKeys.contains(name)) {
                entity.handle(new Config(entity.group, entity.key, event.getPropertyNewValue(name), 0));
            }
        });
    }
}
