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
package org.noear.solon.data.sqlink.core.sqlExt;

import org.noear.solon.data.sqlink.base.expression.SqlOperator;
import org.noear.solon.data.sqlink.base.sqlExt.SqlOperatorMethod;
import org.noear.solon.data.sqlink.core.exception.SqlCalculatesInvokeException;

/**
 * Sql运算符
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqlCalculates {
    /**
     * IS 运算符
     */
    @SqlOperatorMethod(SqlOperator.IS)
    public static <T> boolean is(T t) {
        boom();
        return false;
    }

    /**
     * NOT 运算符
     */
    @SqlOperatorMethod(SqlOperator.NOT)
    public static <T> boolean not(T t) {
        boom();
        return false;
    }

    /**
     * + 运算符
     */
    @SqlOperatorMethod(SqlOperator.PLUS)
    public static <T> T plus(T t1, T t2) {
        boom();
        return (T) new Object();
    }

    /**
     * - 运算符
     */
    @SqlOperatorMethod(SqlOperator.MINUS)
    public static <T> T minus(T t1, T t2) {
        boom();
        return (T) new Object();
    }

    /**
     * * 运算符
     */
    @SqlOperatorMethod(SqlOperator.MUL)
    public static <T> T mul(T t1, T t2) {
        boom();
        return (T) new Object();
    }

    /**
     * / 运算符
     */
    @SqlOperatorMethod(SqlOperator.DIV)
    public static <T> T div(T t1, T t2) {
        boom();
        return (T) new Object();
    }

    /**
     * % 运算符
     */
    @SqlOperatorMethod(SqlOperator.MOD)
    public static <T> T mod(T t1, T t2) {
        boom();
        return (T) new Object();
    }

    /**
     * = 运算符
     */
    @SqlOperatorMethod(SqlOperator.EQ)
    public static <T> boolean eq(T t1, T t2) {
        boom();
        return false;
    }

    /**
     * <> or != 运算符
     */
    @SqlOperatorMethod(SqlOperator.NE)
    public static <T> boolean ne(T t1, T t2) {
        boom();
        return false;
    }

    /**
     * > 运算符
     */
    @SqlOperatorMethod(SqlOperator.GT)
    public static <T> boolean gt(T t1, T t2) {
        boom();
        return false;
    }

    /**
     * < 运算符
     */
    @SqlOperatorMethod(SqlOperator.LT)
    public static <T> boolean lt(T t1, T t2) {
        boom();
        return false;
    }

    /**
     * >= 运算符
     */
    @SqlOperatorMethod(SqlOperator.GE)
    public static <T> boolean ge(T t1, T t2) {
        boom();
        return false;
    }

    /**
     * <= 运算符
     */
    @SqlOperatorMethod(SqlOperator.LE)
    public static <T> boolean LE(T t1, T t2) {
        boom();
        return false;
    }

    /**
     * LIKE 运算符
     */
    @SqlOperatorMethod(SqlOperator.LIKE)
    public static <T> boolean like(T t1, T t2) {
        boom();
        return false;
    }

//    @SqlOperatorMethod(SqlOperator.IN)
//    public static <T> boolean in(T t1, Collection<T> ts)
//    {
//        boom();
//        return false;
//    }
//
//
//    @SqlOperatorMethod(SqlOperator.IN)
//    public static <T> boolean in(T t1, LQuery<T> query)
//    {
//        boom();
//        return false;
//    }

    /**
     * BETWEEN 运算符
     */
    @SqlOperatorMethod(SqlOperator.BETWEEN)
    public static <T> boolean between(T t, T min, T max) {
        boom();
        return false;
    }

    private static void boom() {
        if (win) // if win we win always
        {
            throw new SqlCalculatesInvokeException();
        }
    }

    private static final boolean win = true;
}
