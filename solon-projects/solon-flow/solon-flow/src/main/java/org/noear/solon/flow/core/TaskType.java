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

/**
 * 执行任务类型
 *
 * @author noear
 * @since 3.0
 * */
public enum TaskType {
    /**
     * R, 规则
     */
    rule(0), //0:
    /**
     * F, 函数
     */
    function(1), //1:
    /**
     * A, 参与者
     */
    actor(2); //2:

    private int code;

    public int getCode() {
        return code;
    }

    TaskType(int code) {
        this.code = code;
    }

    public static TaskType valueOf(int code) {
        switch (code) {
            case 1:
                return function;
            case 2:
                return actor;
            default:
                return rule;
        }
    }
}