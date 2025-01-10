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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
* 存储设计::
*
* 0开始节点={id:1, type:0, name:'', }
* 1连线元素={id:2, type:1, name:'', prve:'1', next:'3', condition:'m.user_id,>,12,A;m,F,$ssss(m),E'}
* 2执行节点={id:3, type:2, name:'', task:'F,tag/fun1;R,tag/rule1'}
* 3排他网关={id:4, type:3, name:'', }
* 4并行网关={id:5, type:4, name:'', }
* 5汇聚网关={id:6, type:5, name:'', }
* 9结束节点={id:7, type:6, name:'', }
*
* */

/**
 * 元素对象（节点或线）
 *
 * @author noear
 * @since 3.0
 * */
public class Element {
    private final Chain chain;

    private List<Element> prveNodes, nextNodes, prveLines, nextLines;
    private Condition condition;
    private List<Task> tasks;

    protected Element(Chain chain) {
        this.chain = chain;
    }

    protected String conditionsExpr;
    protected String tasksExpr;

    protected String id;
    protected String name;
    protected ElementType type;      //元素类型
    protected String prveId; //仅line才有
    protected String nextId; //仅line才有

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public ElementType type() {
        return type;
    }

    /**
     * 前一个节点Id（仅line才有）
     */
    public String prveId() {
        return prveId;
    }

    /**
     * 后一个节点Id（仅line才有）
     */
    public String nextId() {
        return nextId;
    }

    public int counter;//计数器，用于计录运行次数

    /**
     * 链
     */
    public Chain chain() {
        return chain;
    }

    /**
     * 前面的节点
     */
    public List<Element> prveNodes() {
        if (prveNodes == null) {
            prveNodes = new ArrayList<>();

            if ((type() == ElementType.start) == false) {
                if (type() == ElementType.line) {
                    prveNodes.add(chain.selectById(prveId()));//by id query
                } else {
                    List<Element> lines = prveLines();
                    lines.forEach(l -> {
                        prveNodes.add(chain.selectById(l.prveId()));//by id query
                    });
                }
            }
        }

        return prveNodes;
    }

    /**
     * 后面的节点
     */
    public List<Element> nextNodes() {
        if (nextNodes == null) {
            nextNodes = new ArrayList<>();

            if ((type == ElementType.stop) == false) {
                if (type() == ElementType.line) {
                    nextNodes.add(chain.selectById(nextId()));//by id query
                } else {
                    List<Element> lines = nextLines();
                    lines.forEach(l -> {
                        nextNodes.add(chain.selectById(l.nextId()));//by id query
                    });
                }
            }
        }

        return nextNodes;

    }

    /**
     * 后面的节点（一个）
     */
    public Element nextNode() {
        return nextNodes().get(0);
    }

    /**
     * 前面的线
     */
    public List<Element> prveLines() {
        if (prveLines == null) {
            prveLines = Collections.emptyList();

            if ((type == ElementType.start || type == ElementType.line) == false) {
                prveLines = chain.selectByNextId(id());//by nextID
            }
        }

        return prveLines;
    }

    /**
     * 后面的线
     */
    public List<Element> nextLines() {
        if (nextLines == null) {
            nextLines = Collections.emptyList();

            if ((type == ElementType.stop || type == ElementType.line) == false) {
                nextLines = chain.selectByPrveId(id());//by prveID
            }
        }

        return nextLines;
    }

    /**
     * 条件；condition:'[{l:"m.user_id",op:">",r:"12",ct:"A"},{l:"m",op:"F",r:"$sss(m)",ct:"E"}]' //m.user_id,>,12,A;m,F,$ssss(m),E
     */
    public Condition condition() {
        if (condition == null) {
            condition = new Condition(conditionsExpr);
        }

        return condition;
    }

    /**
     * 任务列表；task:'F,tag_fun1;R,tag_rule1'
     */
    public List<Task> tasks() {
        if (tasks == null) {
            tasks = Task.parse(tasksExpr);
        }

        return tasks;
    }
}