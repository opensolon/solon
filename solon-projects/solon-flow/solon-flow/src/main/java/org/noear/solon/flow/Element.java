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

import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.List;

/*
* 存储设计::
*
* 0开始节点={id:1, type:0, name:'', }
* 1连线节点={id:2, type:1, name:'', prve:'1', next:'3', condition:'m.user_id,>,12,A;m,F,$ssss(m),E'}
* 2执行节点={id:3, type:2, name:'', tast:'F,tag/fun1;R,tag/rule1'}
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
    private List<Element> _prveNodes, _nextNodes, _prveLines, _nextLines;
    private Condition _condition;
    private List<Task> _tasks;
    private Chain _workflow;

    protected Element(Chain workflow) {
        _workflow = workflow;
    }

    protected String _conditions_str;
    protected String _tasks_str;

    protected String _id;
    protected int _type;      //元素类型
    protected String _prveId; //仅line才有
    protected String _nextId; //仅line才有
    protected String _name;

    public String id() {
        return _id;
    }

    public int type() {
        return _type;
    }

    public String prveId() {
        return _prveId;
    }

    public String nextId() {
        return _nextId;
    }

    public String name() {
        return _name;
    }

    public int counter;//计数器，用于计录运行次数

    /**
     * 获取前面的节点
     */
    public List<Element> prveNodes() {
        if (_prveNodes == null) {
            _prveNodes = new ArrayList<>();

            if ((type() == ElementType.start) == false) {
                if (type() == ElementType.line) {
                    _prveNodes.add(_workflow.selectById(prveId()));//by id query
                } else {
                    List<Element> lines = prveLines();
                    lines.forEach(l -> {
                        _prveNodes.add(_workflow.selectById(l.prveId()));//by id query
                    });
                }
            }
        }

        return _prveNodes;
    }

    /**
     * 获取后面的节点
     */
    public List<Element> nextNodes() {
        if (_nextNodes == null) {
            _nextNodes = new ArrayList<>();

            if ((_type == ElementType.stop) == false) {
                if (type() == ElementType.line) {
                    _nextNodes.add(_workflow.selectById(nextId()));//by id query
                } else {
                    List<Element> lines = nextLines();
                    lines.forEach(l -> {
                        _nextNodes.add(_workflow.selectById(l.nextId()));//by id query
                    });
                }
            }
        }

        return _nextNodes;

    }

    /**
     * 获取后面的节点（一个）
     */
    public Element nextNode() {
        return nextNodes().get(0);
    }

    /**
     * 获取前面的线
     */
    public List<Element> prveLines() {
        if (_prveLines == null) {
            _prveLines = new ArrayList<>();

            if ((_type == ElementType.start || _type == ElementType.line) == false) {
                _prveLines = _workflow.selectByNextId(id());//by nextID
            }
        }

        return _prveLines;
    }

    /**
     * 获取后面的线
     */
    public List<Element> nextLines() {
        if (_nextLines == null) {
            _nextLines = new ArrayList<>();

            if ((_type == ElementType.stop || _type == ElementType.line) == false) {
                _nextLines = _workflow.selectByPrveId(id());//by prveID
            }
        }

        return _nextLines;
    }

    /**
     * 获取条件；condition:'m.user_id,>,12,A;m,F,$ssss(m),E'
     */
    public Condition condition() {
        if (_condition == null) {
            _condition = new Condition(name(), _conditions_str);
        }

        return _condition;
    }

    /**
     * 获取任务列表；tast:'F,tag_fun1;R,tag_rule1'
     */
    public List<Task> tasks() {
        if (_tasks == null) {
            _tasks = new ArrayList<>();

            if (Utils.isEmpty(_tasks_str) == false) {
                String ss[] = _tasks_str.split(";");
                for (int i = 0, len = ss.length; i < len; i++) {
                    Task task = new Task();
                    String[] tt = ss[i].split(",");

                    task._type = "F".equals(tt[0]) ? TaskType.function : TaskType.rule;
                    task._content = tt[1];

                    _tasks.add(task);
                }
            }
        }

        return _tasks;
    }
}