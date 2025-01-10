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

import java.util.ArrayList;
import java.util.List;

/**
 * 执行任务（表达式参考：'F,tag/fun1;R,tag/rule1'）
 *
 * @author noear
 * @since 3.0
 * */
public class Task {
    private String type;
    private String content;

    /**
     * 类型（参考：'R'=rule,'F'=function,'A'=actor）
     */
    public String type() {
        return type;
    }

    /**
     * 内容（参考：'tag/fun1' 或 'tag/rule1'）
     */
    public String content() {
        return content;
    }

    /**
     * 解析
     */
    public static List<Task> parse(String expr) {
        List<Task> list = new ArrayList<>();

        if (Utils.isEmpty(expr) == false) {
            String ss[] = expr.split(";");
            for (int i = 0, len = ss.length; i < len; i++) {
                Task task = new Task();
                String[] tt = ss[i].split(",");

                assert tt.length == 2;

                task.type = tt[0];
                task.content = tt[1];

                list.add(task);
            }
        }

        return list;
    }
}