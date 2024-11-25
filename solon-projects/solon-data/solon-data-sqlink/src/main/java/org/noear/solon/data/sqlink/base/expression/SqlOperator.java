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
package org.noear.solon.data.sqlink.base.expression;

/**
 * sql运算符
 *
 * @author kiryu1223
 * @since 3.0
 */
public enum SqlOperator {
    POS("+", true),                             // +
    NEG("-", true),                             // -
    NOT("NOT", true),                           // !
    COMPL("~", true),                           // ~
    PREINC("++", true),                         // ++ _
    PREDEC("--", true),                         // -- _
    POSTINC("++"),                        // _ ++
    POSTDEC("--"),                        // _ --


    OR("OR"),                             // ||
    AND("AND"),                           // &&
    BITOR("|"),                           // |
    BITXOR("^"),                          // ^
    BITAND("&"),                          // &
    EQ("="),                              // ==
    NE("<>"),                             // !=
    LT("<"),                              // <
    GT(">"),                              // >
    LE("<="),                             // <=
    GE(">="),                             // >=
    SL("<<"),                             // <<
    SR(">>"),                             // >>
    USR(">>>"),                           // >>>
    PLUS("+"),                            // +
    MINUS("-"),                           // -
    MUL("*"),                             // *
    DIV("/"),                             // /
    MOD("%"),                             // %


    BITOR_ASG("|="),                      // |=
    BITXOR_ASG("^="),                     // ^=
    BITAND_ASG("&="),                     // &=

    SL_ASG("<<="),                        // <<=
    SR_ASG(">>="),                        // >>=
    USR_ASG(">>>="),                      // >>>=
    PLUS_ASG("+="),                       // +=
    MINUS_ASG("-="),                      // -=
    MUL_ASG("*="),                        // *=
    DIV_ASG("/="),                        // /=
    MOD_ASG("%="),                        // %=

    LIKE,
    IN,
    IS,
    BETWEEN,
    EXISTS(true),
    ;

    private final String operator;
    private final boolean isLeft;

    SqlOperator() {
        this.operator = name();
        this.isLeft = false;
    }

    SqlOperator(boolean isLeft) {
        this.operator = name();
        this.isLeft = isLeft;
    }

    SqlOperator(String operator) {
        this.operator = operator;
        this.isLeft = false;
    }

    SqlOperator(String operator, boolean isLeft) {
        this.operator = operator;
        this.isLeft = isLeft;
    }

    public String getOperator() {
        return operator;
    }

    public boolean isLeft() {
        return isLeft;
    }
}
