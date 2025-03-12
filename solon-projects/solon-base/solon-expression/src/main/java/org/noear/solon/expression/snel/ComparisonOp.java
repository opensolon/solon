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
package org.noear.solon.expression.snel;

/**
 * 比较操作符
 *
 * @author noear
 * @since 3.1
 */
public enum ComparisonOp {
    lt("<"),  // <
    lte("<="), // <=
    gt(">"),  // >
    gte(">="), // >=
    eq("=="),  // ==
    neq("!="), // !=
    lk("LIKE"),  // like
    nlk("NOT LIKE"), // not like
    in("IN"),  // in
    nin("NO IN"), // not in
    ;

    ComparisonOp(String code) {
        this.code = code;
    }

    private final String code;

    /**
     * 代号
     */
    public String getCode() {
        return code;
    }

    /**
     * 解析
     */
    public static ComparisonOp parse(String op) {
        switch (op) {
            case "<":
                return lt;
            case "<=":
                return lte;
            case ">":
                return gt;
            case ">=":
                return gte;
            case "==":
                return eq;
            case "!=":
                return neq;
            case "LIKE":
                return lk;
            case "NOT LIKE":
                return nlk;
            case "IN":
                return in;
            case "NOT IN":
                return nin;
            default:
                throw new IllegalArgumentException("Invalid comparison operator: " + op);
        }
    }
}