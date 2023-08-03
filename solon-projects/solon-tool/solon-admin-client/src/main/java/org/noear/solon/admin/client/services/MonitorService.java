package org.noear.solon.admin.client.services;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.admin.client.data.Detector;
import org.noear.solon.annotation.Component;
import org.noear.solon.health.detector.DetectorManager;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 监视器服务
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Slf4j
@Component
public class MonitorService {

    /**
     * 获取所有监视器信息
     *
     * @return 所有监视器信息
     */
    public Collection<Detector> getMonitors() {
        return DetectorManager.all()
                .parallelStream()
                .map(it -> new Detector(it.getName(), it.getInfo()))
                .collect(Collectors.toList());
    }

}
