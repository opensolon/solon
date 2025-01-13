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

import org.noear.liquor.eval.CodeSpec;
import org.noear.liquor.eval.Exprs;
import org.noear.liquor.eval.Scripts;
import org.noear.solon.Solon;
import org.noear.solon.flow.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 简单的流驱动器
 *
 * @author noear
 * @since 3.0
 * */
public class SimpleFlowDriver implements FlowDriver {
    private static final Logger log = LoggerFactory.getLogger(SimpleFlowDriver.class);
    private static final SimpleFlowDriver instance = new SimpleFlowDriver();

    public static SimpleFlowDriver getInstance() {
        return instance;
    }

    /**
     * 是否为组件
     */
    protected boolean isChain(String description) {
        return description.startsWith("#");
    }

    /**
     * 是否为组件
     */
    protected boolean isComponent(String description) {
        return description.startsWith("@");
    }

    @Override
    public void onNodeStart(Context context, Node node) {
        log.debug("on-node-start: chain={}, node={}", node.chain().id(), node);
    }

    @Override
    public void onNodeEnd(Context context, Node node) {
        log.debug("on-node-end: chain={}, node={}", node.chain().id(), node);
    }

    @Override
    public boolean handleCondition(Context context, Condition condition) throws Throwable {
        if (isComponent(condition.description())) {
            //按组件运行
            String beanName = condition.description().substring(1);
            ConditionComponent component = Solon.context().getBean(beanName);

            if (component == null) {
                throw new IllegalStateException("The condition '" + beanName + "' not exist");
            } else {
                return component.test(context, condition.link());
            }
        } else {
            //按脚本运行
            return (boolean) Exprs.eval(condition.description(), context.params());
        }
    }

    @Override
    public void handleTask(Context context, Task task) throws Throwable {
        if (tryIfChainTask(context, task)) {
            return;
        }

        if (tryIfComponentTask(context, task)) {
            return;
        }

        tryAsScriptTask(context, task);
    }

    /**
     * 尝试如果是链则运行
     */
    protected boolean tryIfChainTask(Context context, Task task) throws Throwable {
        if (isChain(task.description())) {
            //调用其它链
            String chainId = task.description().substring(1);
            context.engine().eval(chainId, context);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 尝试如果是组件则运行
     */
    protected boolean tryIfComponentTask(Context context, Task task) throws Throwable {
        if (isComponent(task.description())) {
            //按组件运行
            String beanName = task.description().substring(1);
            TaskComponent component = Solon.context().getBean(beanName);

            if (component == null) {
                throw new IllegalStateException("The task component '" + beanName + "' not exist");
            } else {
                component.run(context, task.node());
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 尝试作为脚本运行
     */
    protected void tryAsScriptTask(Context context, Task task) throws Throwable {
        //按脚本运行
        Map<String, Object> argsMap = new LinkedHashMap<>();
        argsMap.put("context", context);
        argsMap.putAll(context.params());

        CodeSpec codeSpec = new CodeSpec(task.description());
        Object[] args = codeSpec.bind(argsMap);
        Scripts.eval(codeSpec, args);
    }
}