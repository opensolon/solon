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
package org.noear.solon.flow.core;

import org.noear.solon.Utils;


/**
 * 条件（一般用于分支条件）
 *
 * @author noear
 * @since 3.0
 * */
public class Condition {
    private final NodeLink link;
    private final String description;

    /**
     * 附件（按需定制使用）
     */
    public Object attachment;//如果做扩展解析，用作存储位；（不解析，定制性更强）

    /**
     * @param link        所属链接
     * @param description 条件描述
     */
    public Condition(NodeLink link, String description) {
        this.link = link;
        this.description = description;
    }

    /**
     * 所属链接
     */
    public NodeLink link() {
        return link;
    }

    /**
     * 描述（示例："(a,>,12) and (b,=,1)" 或 "a=12 && b=1" 或 "[{l:'a',p:'>',r:'12'}...]"）
     */
    public String description() {
        return description;
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return Utils.isEmpty(description);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{" +
                    "nextId='" + link.nextId() + '\'' +
                    ", description=null" +
                    '}';
        } else {
            return "{" +
                    "nextId='" + link.nextId() + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}