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
    public void exec(ChainDriver driver, Chain chain) throws Exception {
        exec(driver, chain, null, -1);
    }

    /**
     * 执行
     *
     * @param driver 驱动器
     * @param chain   链
     * @param startId 开始Id
     * @param depth   执行深度
     */
    public void exec(ChainDriver driver, Chain chain, String startId, int depth) throws Exception {
        Element start;
        if (startId == null) {
            start = chain.start();
        } else {
            start = chain.selectById(startId);
        }

        assert start != null;

        node_run(driver, start, depth);
    }

    /**
     * 检查条件
     */
    private boolean condition_check(ChainDriver driver, Element line, Condition condition) throws Exception {
        return driver.handleCondition(line, condition);
    }

    /**
     * 执行任务
     */
    private void task_exec(ChainDriver driver, Element node, Task task) throws Exception {
        driver.handleTask(node, task);
    }

    /**
     * 运行节点
     */
    private void node_run(ChainDriver driver, Element node, int depth) throws Exception {
        if (driver.isInterrupt()) { //如果取消，就不再执行了
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
                node_run(driver, node.nextNode(), depth);
            }
            break;
            case stop: {
                //无动作
            }
            break;
            case execute: {
                task_exec(driver, node, node.task());

                node_run(driver, node.nextNode(), depth);
            }
            break;
            case exclusive: {
                exclusive_run(driver, node, depth);
            }
            break;
            case parallel: {
                parallel_run(driver, node, depth);
            }
            break;
            case converge: {
                converge_run(driver, node, depth);
            }
            break;
        }
    }

    /**
     * 运行排他网关
     */
    private void exclusive_run(ChainDriver driver, Element node, int depth) throws Exception {
        List<Element> lines = node.nextLines();
        Element def_line = null;
        for (Element l : lines) {
            if (l.condition().isEmpty()) {
                def_line = l;
            } else {
                if (condition_check(driver, l, l.condition())) {
                    node_run(driver, l.nextNode(), depth);
                    return;
                }
            }
        }

        node_run(driver, def_line.nextNode(), depth);
    }

    /**
     * 运行并行网关
     */
    private void parallel_run(ChainDriver driver, Element node, int depth) throws Exception {
        for (Element n : node.nextNodes()) {
            node_run(driver, n, depth);
        }
    }

    /**
     * 运行汇聚网关//起到等待和卡位的作用；
     */
    private void converge_run(ChainDriver driver, Element node, int depth) throws Exception {
        int count = driver.counterIncr(node.id());//运行次数累计
        if (node.prveLines().size() > count) { //等待所有支线计数完成
            return;
        }

        node_run(driver, node.nextNode(), depth); //然后到下一个节点
    }
}