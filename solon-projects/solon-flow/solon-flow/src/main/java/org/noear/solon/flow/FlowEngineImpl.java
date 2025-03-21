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
package org.noear.solon.flow;

import org.noear.solon.Utils;
import org.noear.solon.core.util.RankEntity;
import org.noear.solon.flow.driver.SolonChainDriver;
import org.noear.solon.flow.intercept.ChainInterceptor;
import org.noear.solon.flow.intercept.ChainInvocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流引擎实现
 *
 * @author noear
 * @since 3.0
 */
class FlowEngineImpl implements FlowEngine {
    protected final Map<String, Chain> chainMap = new ConcurrentHashMap<>();
    protected final Map<String, ChainDriver> driverMap = new ConcurrentHashMap<>();
    protected final List<RankEntity<ChainInterceptor>> interceptorList = new ArrayList<>();

    public FlowEngineImpl() {
        //默认驱动器
        driverMap.put("", SolonChainDriver.getInstance());
    }

    @Override
    public void addInterceptor(ChainInterceptor interceptor, int index) {
        interceptorList.add(new RankEntity<>(interceptor, index));
        Collections.sort(interceptorList);
    }

    @Override
    public void register(String name, ChainDriver driver) {
        if (driver != null) {
            driverMap.put(name, driver);
        }
    }

    @Override
    public void unregister(String name) {
        if (Utils.isNotEmpty(name)) {
            driverMap.remove(name);
        }
    }

    @Override
    public void load(Chain chain) {
        chainMap.put(chain.id(), chain);
    }

    @Override
    public void unload(String chainId) {
        chainMap.remove(chainId);
    }

    @Override
    public Collection<Chain> chains() {
        return chainMap.values();
    }

    /**
     * 评估
     *
     * @param chainId 链
     * @param context 上下文
     */
    @Override
    public void eval(String chainId, String startId, int depth, ChainContext context) throws Throwable {
        Chain chain = chainMap.get(chainId);
        if (chain == null) {
            throw new IllegalArgumentException("No chain found for id: " + chainId);
        }

        eval(chain, startId, depth, context);
    }

    /**
     * 评估
     *
     * @param chain   链
     * @param startId 开始Id
     * @param depth   执行深度
     * @param context 上下文
     */
    @Override
    public void eval(Chain chain, String startId, int depth, ChainContext context) throws Throwable {
        Node start;
        if (startId == null) {
            start = chain.start();
        } else {
            start = chain.getNode(startId);
        }

        if (start == null) {
            throw new IllegalArgumentException("The start node was not found.");
        }

        if (context.engine == null) {
            context.engine = this;
        }

        ChainDriver driver = driverMap.get(chain.driver());

        if (driver == null) {
            throw new IllegalArgumentException("No driver found for: '" + chain.driver() + "'");
        }

        //开始执行
        new ChainInvocation(driver, context, start, depth, this.interceptorList, this::evalDo).invoke();
    }

    /**
     * 执行评估
     */
    protected void evalDo(ChainInvocation inv) throws Throwable {
        node_run(inv.getDriver(), inv.getContext(), inv.getStartNode(), inv.getEvalDepth());
    }

    /**
     * 条件检测
     */
    private boolean condition_test(ChainDriver driver, ChainContext context, Condition condition, boolean def) throws Throwable {
        if (Utils.isNotEmpty(condition.description())) {
            return driver.handleTest(context, condition);
        } else {
            return def;
        }
    }

    /**
     * 执行任务
     */
    private void task_exec(ChainDriver driver, ChainContext context, Node node) throws Throwable {
        //尝试检测条件；缺省为 true
        if (condition_test(driver, context, node.when(), true)) {
            //起到触发事件的作用 //处理方会“过滤”空任务
            driver.handleTask(context, node.task());
        }
    }

    /**
     * 运行节点
     */
    private boolean node_run(ChainDriver driver, ChainContext context, Node node, int depth) throws Throwable {
        if (node == null) {
            return false;
        }

        //如果阻断，当前分支不再后流
        if (context.isInterrupted()) {
            //重置阻断（不影响别的分支）
            context.interrupt(false);
            return false;
        }
        //如果停止
        if (context.isStopped()) {
            return false;
        }

        //执行深度控制
        if (depth == 0) {
            return true;
        } else {
            depth--;
        }

        //节点运行之前事件
        driver.onNodeStart(context, node);

        //如果阻断，就不再执行了（onNodeBefore 可能会触发中断）
        if (context.isInterrupted()) {
            //重置阻断（不影响别的分支）
            context.interrupt(false);
            return false;
        }
        //如果停止
        if (context.isStopped()) {
            return false;
        }

        boolean node_end = true;

        switch (node.type()) {
            case start:
                //转到下个节点
                node_run(driver, context, node.nextNode(), depth);
                break;
            case end:
                break;
            case execute:
                //尝试执行任务（可能为空）
                task_exec(driver, context, node);
                //转到下个节点
                node_run(driver, context, node.nextNode(), depth);
                break;
            case inclusive: //包容网关（多选）
                node_end = inclusive_run(driver, context, node, depth);
                break;
            case exclusive: //排他网关（单选）
                node_end = exclusive_run(driver, context, node, depth);
                break;
            case parallel: //并行网关（全选）
                node_end = parallel_run(driver, context, node, depth);
                break;
        }

        //节点运行之后事件
        if (node_end) {
            driver.onNodeEnd(context, node);
        }


        return node_end;
    }

    /**
     * 运行包容网关
     */
    private boolean inclusive_run(ChainDriver driver, ChainContext context, Node node, int depth) throws Throwable {
        Stack<Integer> inclusive_stack = context.counter().stack(node.chain(), "inclusive_run");

        //::流入
        if (node.prveLinks().size() > 1) { //如果是多个输入链接（尝试等待）
            if (inclusive_stack.size() > 0) {
                int start_size = inclusive_stack.peek();
                int in_size = context.counter().incr(node.chain(), node.id());//运行次数累计
                if (start_size > in_size) { //等待所有支线流入完成
                    return false;
                }

                //聚合结束，取消这个栈节点
                inclusive_stack.pop();
            }
            //如果没有 gt 0，说明之前还没有流出的
        }

        //::流出
        Link def_line = null;
        List<Link> matched_lines = new ArrayList<>();

        for (Link l : node.nextLinks()) {
            if (l.condition().isEmpty()) {
                def_line = l;
            } else {
                if (condition_test(driver, context, l.condition(), false)) {
                    matched_lines.add(l);
                }
            }
        }

        if (matched_lines.size() > 0) {
            //记录流出数量
            inclusive_stack.push(matched_lines.size());

            //执行所有满足条件
            for (Link l : matched_lines) {
                node_run(driver, context, l.nextNode(), depth);
            }
        } else if (def_line != null) {
            //不需要，记录流出数量
            //如果有默认
            node_run(driver, context, def_line.nextNode(), depth);
        }

        return true;
    }

    /**
     * 运行排他网关
     */
    private boolean exclusive_run(ChainDriver driver, ChainContext context, Node node, int depth) throws Throwable {
        //::流出
        Link def_line = null; //默认线
        for (Link l : node.nextLinks()) {
            if (l.condition().isEmpty()) {
                def_line = l;
            } else {
                if (condition_test(driver, context, l.condition(), false)) {
                    //执行第一个满足条件
                    node_run(driver, context, l.nextNode(), depth);
                    return true; //结束
                }
            }
        }

        if (def_line != null) {
            //如果有默认
            node_run(driver, context, def_line.nextNode(), depth);
        }

        return true;
    }

    /**
     * 运行并行网关
     */
    private boolean parallel_run(ChainDriver driver, ChainContext context, Node node, int depth) throws Throwable {
        //::流入
        int count = context.counter().incr(node.chain(), node.id());//运行次数累计
        if (node.prveLinks().size() > count) { //等待所有支线计数完成
            return false;
        }

        //恢复计数
        context.counter().set(node.chain(), node.id(), 0);

        //::流出
        for (Node n : node.nextNodes()) {
            node_run(driver, context, n, depth);
        }

        return true;
    }
}