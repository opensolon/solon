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
package org.noear.solon.flow.driver;

import org.noear.solon.Solon;
import org.noear.solon.flow.*;

/**
 * Solon 组件管理链驱动器
 *
 * @author noear
 * @since 3.0
 * */
public class SolonChainDriver extends AbstractChainDriver {
    private static final SolonChainDriver instance = new SolonChainDriver();

    public static SolonChainDriver getInstance() {
        return instance;
    }

    @Override
    protected void tryAsComponentTask(ChainContext context, Task task, String description) throws Throwable {
        //按组件运行
        String beanName = description.substring(1);
        TaskComponent component = Solon.context().getBean(beanName);

        if (component == null) {
            throw new IllegalStateException("The task component '" + beanName + "' not exist");
        } else {
            component.run(context, task.node());
        }
    }
}