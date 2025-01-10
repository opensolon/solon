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
package org.noear.solon.flow.core;

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
    public void exec(ChainContext context, Chain chain) throws Exception {
        exec(context, chain, null, -1);
    }

    /**
     * 执行
     *
     * @param context 上下文
     * @param chain   链
     * @param startId 开始Id
     * @param depth   执行深度
     */
    public void exec(ChainContext context, Chain chain, String startId, int depth) throws Exception {
        Element start;
        if (startId == null) {
            start = chain.start();
        } else {
            start = chain.selectById(startId);
        }

        assert start != null;

        node_run(context, start, depth);
    }

    /**
     * 检查条件
     */
    private boolean condition_check(ChainContext context, Element line, Condition condition) throws Exception {
        return context.handleCondition(line, condition);
    }

    /**
     * 执行任务
     */
    private void task_exec(ChainContext context, Element node, Task task) throws Exception {
        context.handleTask(node, task);
    }

    /**
     * 运行节点
     */
    private void node_run(ChainContext context, Element node, int depth) throws Exception {
        if (context.isCancel()) { //如果取消，就不再执行了
            return;
        }

        //执行深度控制
        if (depth == 0) {
            return;
        } else {
            depth--;
        }

        switch (node.type()) {
            case start: {
                node_run(context, node.nextNode(), depth);
            }
            break;
            case stop: {
                //无动作
            }
            break;
            case execute: {
                task_exec(context, node, node.task());

                node_run(context, node.nextNode(), depth);
            }
            break;
            case exclusive: {
                exclusive_run(context, node, depth);
            }
            break;
            case parallel: {
                parallel_run(context, node, depth);
            }
            break;
            case converge: {
                converge_run(context, node, depth);
            }
            break;
        }
    }

    /**
     * 运行排他网关
     */
    private void exclusive_run(ChainContext context, Element node, int depth) throws Exception {
        List<Element> lines = node.nextLines();
        Element def_line = null;
        for (Element l : lines) {
            if (l.condition().isEmpty()) {
                def_line = l;
            } else {
                if (condition_check(context, l, l.condition())) {
                    node_run(context, l.nextNode(), depth);
                    return;
                }
            }
        }

        node_run(context, def_line.nextNode(), depth);
    }

    /**
     * 运行并行网关
     */
    private void parallel_run(ChainContext context, Element node, int depth) throws Exception {
        for (Element n : node.nextNodes()) {
            node_run(context, n, depth);
        }
    }

    /**
     * 运行汇聚网关//起到等待和卡位的作用；
     */
    private void converge_run(ChainContext context, Element node, int depth) throws Exception {
        int count = context.counterIncr(node.id());//运行次数累计
        if (node.prveLines().size() > count) { //等待所有支线计数完成
            return;
        }

        node_run(context, node.nextNode(), depth); //然后到下一个节点
    }
}