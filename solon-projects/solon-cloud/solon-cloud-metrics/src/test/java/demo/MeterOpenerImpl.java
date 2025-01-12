/*
 * Copyright 2017-2025 noear.org and authors
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
package demo;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.cloud.metrics.export.MeterOpener;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2023/8/5 created
 */
@Configuration
public class MeterOpenerImpl implements MeterOpener {
    SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Bean
    public MeterRegistry registry(){
        return meterRegistry;
    }

    @Override
    public String path() {
        return "/demo/xxxx";
    }

    @Override
    public boolean isSupported(AppContext appContext) {
        //不支持开放输出
        return false;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        //meterRegistry
    }
}
