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
package features;

import org.noear.solon.annotation.Bean;
import org.noear.solon.core.handle.Result;
import org.noear.solon.health.HealthChecker;

/**
 * @author noear 2021/10/9 created
 */
//@Configuration
public class Config {
    @Bean
    public void healthInit(){
        HealthChecker.addIndicator("preflight", Result::succeed);
        HealthChecker.addIndicator("test", Result::failure);
        HealthChecker.addIndicator("boom", () -> {
            throw new IllegalStateException();
        });
    }
}
