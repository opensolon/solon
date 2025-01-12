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
package org.noear.solon.scheduling.command;

import org.noear.solon.core.BeanWrap;

/**
 * 命令执行器类原型代理（支持非单例运行）
 *
 * @author noear
 * @since 2.9
 */
public class CommandExecutorProxy implements CommandExecutor {
    private BeanWrap target;

    public CommandExecutorProxy(BeanWrap target) {
        this.target = target;
    }

    public BeanWrap getTarget() {
        return target;
    }

    @Override
    public void execute(String command) throws Throwable {
        ((CommandExecutor) target.get()).execute(command);
    }
}
