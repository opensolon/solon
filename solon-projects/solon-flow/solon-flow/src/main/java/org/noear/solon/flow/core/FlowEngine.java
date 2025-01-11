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

import org.noear.solon.Utils;
import org.noear.solon.lang.Preview;

import java.util.ArrayList;
import java.util.List;

/**
 * 流引擎
 *
 * @author noear
 * @since 3.0
 * */
@Preview("3.0")
public class FlowEngine {
    /**
     * 评估
     *
     * @param context 上下文
     * @param chain   链
     */
    public void eval(ChainContext context, Chain chain) throws Throwable {
        eval(context, chain, null, -1);
    }

    /**
     * 评估
     *
     * @param context 上下文
     * @param chain   链
     * @param startId 开始Id
     * @param depth   执行深度
     */
    public void eval(ChainContext context, Chain chain, String startId, int depth) throws Throwable {
        Node start;
        if (startId == null) {
            start = chain.start();
        } else {
            start = chain.selectById(startId);
        }

        assert start != null;

        node_run(context, chain, start, depth);
    }

    /**
     * 检查条件
     */
    private boolean condition_check(ChainContext context, Chain chain, Condition condition) throws Throwable {
        if (Utils.isNotEmpty(condition.expr())) {
            return chain.driver().handleCondition(context, condition);
        } else {
            return false;
        }
    }

    /**
     * 执行任务
     */
    private void task_exec(ChainContext context, Chain chain, Task task) throws Throwable {
        if (Utils.isNotEmpty(task.expr())) {
            chain.driver().handleTask(context, task);
        }
    }

    /**
     * 运行节点
     */
    private void node_run(ChainContext context, Chain chain, Node node, int depth) throws Throwable {
        if (context.isInterrupted()) { //如果中断，就不再执行了
            return;
        }

        //执行深度控制
        if (depth == 0) {
            return;
        } else {
            depth--;
        }

        switch (node.type()) {
            case start:
                //执行任务
                task_exec(context, chain, node.task());
                //转到下个节点
                node_run(context, chain, node.nextNode(), depth);
                break;
            case end:
                //无动作
                break;
            case execute: {
                //执行任务
                task_exec(context, chain, node.task());
                //转到下个节点
                node_run(context, chain, node.nextNode(), depth);
                break;
            }

            case inclusive: //包容网关（多选）
                inclusive_run(context, chain, node, depth);
                break;
            case exclusive: //排他网关（单选）
                exclusive_run(context, chain, node, depth);
                break;
            case parallel: //并行网关（全选）
                parallel_run(context, chain, node, depth);
                break;
        }
    }

    /**
     * 运行包容网关
     */
    private void inclusive_run(ChainContext context, Chain chain, Node node, int depth) throws Throwable {
        final String token_key = "$inclusive_size";

        //流入
        int count = context.counterIncr(node.id());//运行次数累计
        if (context.counterGet(token_key) > count) { //等待所有支线计数完成
            return;
        }

        //流出
        Link def_line = null;
        List<Link> matched_lines = new ArrayList<>();

        for (Link l : node.nextLines()) {
            if (l.condition().isEmpty()) {
                def_line = l;
            } else {
                if (condition_check(context, chain, l.condition())) {
                    matched_lines.add(l);
                }
            }
        }


        context.counterSet(token_key, matched_lines.size());
        if (matched_lines.size() > 0) {
            //执行所有满足条件
            for (Link l : matched_lines) {
                node_run(context, chain, l.nextNode(), depth);
            }
        } else if (def_line != null) {
            //如果有默认
            node_run(context, chain, def_line.nextNode(), depth);
        }
    }

    /**
     * 运行排他网关
     */
    private void exclusive_run(ChainContext context, Chain chain, Node node, int depth) throws Throwable {
        List<Link> lines = node.nextLines();
        Link def_line = null;
        for (Link l : lines) {
            if (l.condition().isEmpty()) {
                def_line = l;
            } else {
                if (condition_check(context, chain, l.condition())) {
                    //执行第一个满足条件
                    node_run(context, chain, l.nextNode(), depth);
                    return;
                }
            }
        }

        if (def_line != null) {
            //如果有默认
            node_run(context, chain, def_line.nextNode(), depth);
        }
    }

    /**
     * 运行并行网关
     */
    private void parallel_run(ChainContext context, Chain chain, Node node, int depth) throws Throwable {
        //流入
        int count = context.counterIncr(node.id());//运行次数累计
        if (node.prveLinesCount(null) > count) { //等待所有支线计数完成
            return;
        }

        //流出
        for (Node n : node.nextNodes()) {
            node_run(context, chain, n, depth);
        }
    }
}