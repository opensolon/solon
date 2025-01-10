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
package org.noear.solon.flow;

import java.util.List;

/**
 * 链执行器
 *
 * @author noear
 * @since 3.0
 * */
public class ChainExecutor {
    /**
     * 执行
     */
    public void exec(Chain chain, ChainContext context) throws Exception {
        Element start = chain.start();

        node_run(context, start);
    }

    /**
     * 检查条件
     */
    private boolean condition_check(ChainContext context, Condition condition) throws Exception {
        return context.condition_handle(condition);
    }

    /**
     * 执行任务
     */
    private void task_exec(ChainContext context, List<Task> tasks) throws Exception {
        context.task_handle(tasks);
    }

    /**
     * 运行节点
     */
    private void node_run(ChainContext context, Element node) throws Exception {
        if (context.is_cancel()) { //如果取消，就不再执行了
            return;
        }

        switch (node.type()) {
            case ElementType.start: {
                node_run(context, node.nextNode());
            }
            break;
            case ElementType.stop: {
                //无动作
            }
            break;
            case ElementType.execute: {
                task_exec(context, node.tasks());

                node_run(context, node.nextNode());
            }
            break;
            case ElementType.exclusive: {
                exclusive_run(context, node);
            }
            break;
            case ElementType.parallel: {
                parallel_run(context, node);
            }
            break;
            case ElementType.converge: {
                converge_run(context, node);
            }
            break;
        }
    }

    /**
     * 运行排他网关
     */
    private void exclusive_run(ChainContext context, Element node) throws Exception {
        List<Element> lines = node.nextLines();
        Element def_line = null;
        for (Element l : lines) {
            if (l.condition().isEmpty()) {
                def_line = l;
            } else {
                if (condition_check(context, l.condition())) {
                    node_run(context, l.nextNode());
                    return;
                }
            }
        }

        node_run(context, def_line.nextNode());
    }

    /**
     * 运行并行网关
     */
    private void parallel_run(ChainContext context, Element node) throws Exception {
        for (Element n : node.nextNodes()) {
            node_run(context, n);
        }
    }

    /**
     * 运行汇聚网关//起到等待和卡位的作用；
     */
    private void converge_run(ChainContext context, Element node) throws Exception {
        node.counter++; //运行次数累计
        if (node.prveLines().size() > node.counter) { //等待所有支线计数完成
            return;
        }

        node_run(context, node.nextNode()); //然后到下一个节点
    }
}