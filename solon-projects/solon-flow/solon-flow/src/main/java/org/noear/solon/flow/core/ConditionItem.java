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

import org.noear.snack.ONode;
import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件项（结构：左侧,操作符,右侧,连接符;...）
 *
 * @author noear
 * @since 3.0
 * */
public class ConditionItem {
    /**
     * 左侧
     */
    protected String left; //左侧
    /**
     * 运算符
     */
    protected String operator;//运算符 //=，>，>=，<，<=，L(包含)，F(函数)
    /**
     * 右则
     */
    protected String right; //右则
    /**
     * 连接符
     */
    protected String connect; //连接符 //'', '||', '&&'


    public String leftAsJs() {
        if ("m".equals(left) == false) {
            return "m." + left + "()";
        } else {
            return left;
        }
    }

    public String leftAsSql() {
        if (left.indexOf(".") > 0) {
            return left.split(" ")[0];
        } else {
            return "m." + left.split(" ")[0];
        }
    }

    public String operatorAsJs() {
        if ("=".equals(operator)) {
            return "==";
        } else {
            return operator;
        }
    }

    public String operatorAsSql() {
        if ("==".equals(operator)) {
            return "=";
        } else {
            return operator;
        }
    }

    public String right() {
        return right;
    } //右则

    public String connect() {
        return connect;
    } //连接符 //'', '||', '&&'


    /**
     * 解析
     * */
    public static List<ConditionItem> parse(String expr) {
        List<ConditionItem> list = null;

        if (Utils.isEmpty(expr) == false) {
            list = new ArrayList<>();

            ONode oExpr = ONode.load(expr);

            ONode d = null;
            for (String k : oExpr.obj().keySet()) {
                d = oExpr.get(k);
                ConditionItem cond = new ConditionItem();

                cond.left = d.get("l").getString();

                if (Utils.isEmpty(cond.left)) {
                    continue;
                }

                cond.operator = d.get("op").getString();
                cond.right = d.get("r").getString();
                cond.connect = d.get("ct").getString();

                if ("=".equals(cond.operator)) {
                    cond.operator = "==";
                }

                list.add(cond);
            }
        }

        return list;
    }
}