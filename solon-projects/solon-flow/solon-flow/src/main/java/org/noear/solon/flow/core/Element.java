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
import java.util.Map;

/*
* 存储设计::
*
* 0开始节点={id:1, type:0, title:'', }
*
* 1连接线段={id:2, type:1, title:'', prve:'1', next:'3', condition:'(m.user_id,>,12) && (m,F,$ssss(m))'} //A=and,O=or,E=end
*
* 2执行节点={id:3, type:2, title:'', task:'F,tag/fun1;R,tag/rule1'}
*
* 3排他网关={id:4, type:3, title:'', }
* 4并行网关={id:5, type:4, title:'', }
* 5汇聚网关={id:6, type:5, title:'', }
*
* 9结束节点={id:7, type:6, title:'', }
* */

/**
 * 元素对象（节点或线）
 *
 * @author noear
 * @since 3.0
 * */
public class Element {
    private final transient Chain chain;

    private final String id;
    private final String title;
    private final ElementType type;      //元素类型
    private final Map<String, Object> meta; //元信息

    private final String prveId; //仅line才有
    private final String nextId; //仅line才有
    private final String conditionExpr;

    private final String taskExpr;

    private List<Element> prveNodes, nextNodes, prveLines, nextLines;
    private Condition condition;
    private Task task;

    protected Element(Chain chain, String id, String title, ElementType type, Map<String, Object> meta, String prveId, String nextId, String conditionExpr, String taskExpr) {
        this.chain = chain;

        this.id = id;
        this.title = (title == null ? id : title);
        this.type = type;
        this.meta = meta;

        this.prveId = prveId;
        this.nextId = nextId;
        this.conditionExpr = conditionExpr;
        this.taskExpr = taskExpr;
    }


    /**
     * 所属链
     */
    public Chain chain() {
        return chain;
    }

    /**
     * 标识
     */
    public String id() {
        return id;
    }

    /**
     * 显示标题
     */
    public String title() {
        return title;
    }

    /**
     * 类型
     */
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
     * 条件
     */
    public Condition condition() {
        if (condition == null) {
            condition = new Condition(this, conditionExpr);
        }

        return condition;
    }

    /**
     * 任务
     */
    public Task task() {
        if (task == null) {
            task = new Task(this, taskExpr);
        }

        return task;
    }

    @Override
    public String toString() {
        if (type == ElementType.line) {
            return "{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", meta=" + meta +
                    ", prveId=" + prveId +
                    ", nextId=" + nextId +
                    ", conditionExpr=" + conditionExpr +
                    '}';

        } else if (type == ElementType.execute) {
            return "{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", meta=" + meta +
                    ", taskExpr=" + taskExpr +
                    '}';
        } else {
            return "{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", meta=" + meta +
                    '}';
        }
    }
}