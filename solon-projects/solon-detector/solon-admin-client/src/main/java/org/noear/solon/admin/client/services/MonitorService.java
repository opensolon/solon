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
