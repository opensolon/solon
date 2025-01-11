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

import java.util.Map;

/**
 * 连接
 *
 * @author noear
 * @since 3.0
 */
public class Link {
    private final Chain chain;
    private final String fromId;
    private final LinkDecl decl;

    private Node prveNode, nextNode;
    private Condition condition;

    public Link(Chain chain, String fromId, LinkDecl decl) {
        this.chain = chain;
        this.fromId = fromId;
        this.decl = decl;
    }

    public Chain chain() {
        return chain;
    }

    public Map<String,Object> meta(){
        return decl.meta();
    }

    public String prveId() {
        return fromId;
    }

    public String nextId() {
        return decl.toId();
    }

    /**
     * 条件
     */
    public Condition condition() {
        if (condition == null) {
            condition = new Condition(this, decl.condition());
        }

        return condition;
    }

    /**
     * 前面的节点
     */
    public Node prveNode() {
        if (prveNode == null) {
            prveNode = chain.selectById(prveId()); //by id query
        }

        return prveNode;
    }

    /**
     * 后面的节点
     */
    public Node nextNode() {
        if (nextNode == null) {
            nextNode = chain.selectById(nextId()); //by id query
        }

        return nextNode;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + decl.title() + '\'' +
                ", prveId='" + prveId() + '\'' +
                ", prveId='" + prveId() + '\'' +
                ", meta=" + decl.meta() +
                ", condition='" + decl.condition() + '\'' +
                '}';
    }
}
