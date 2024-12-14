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
package org.noear.solon.rx.r2dbc;

import org.noear.solon.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Sql 代码构建器
 *
 * @author noear
 * @since 3.0
 */
public class SqlBuilder implements SqlSpec {
    //当前数据
    private StringBuilder c_builder = new StringBuilder(200);
    private List<Object> c_args = new ArrayList<Object>();

    //备份数据
    private StringBuilder b_builder = new StringBuilder();
    private List<Object> b_args = new ArrayList<>();

    /**
     * 查找位置
     */
    public int indexOf(String str) {
        return c_builder.indexOf(str);
    }

    /**
     * 截取子字符串
     */
    public String substring(int start) {
        return c_builder.substring(start);
    }

    /**
     * 截取子字符串
     */
    public String substring(int start, int end) {
        return c_builder.substring(start, end);
    }

    /**
     * 长度
     */
    public int length() {
        return c_builder.length();
    }

    /**
     * 清空
     */
    public void clear() {
        c_builder.delete(0, c_builder.length());
        c_args.clear();
    }

    /**
     * 备分状态
     */
    public SqlBuilder backup() {
        b_builder.append(c_builder);
        b_args.addAll(c_args);
        return this;
    }

    /**
     * 还原状态
     */
    public SqlBuilder restore() {
        clear();
        c_builder.append(b_builder);
        c_args.addAll(b_args);
        return this;
    }

    /**
     * 插入到某个位置前面
     */
    public SqlBuilder insert(int offset, String sql, Object... args) {
        if (offset < 0) {
            //如果找不到位置；加到后面
            append(sql, args);
        } else {
            SqlPartBuilder tmp = new SqlPartBuilder(sql, args);

            if (offset == 0) {
                //如果是0位
                c_builder.insert(0, tmp.sql);
                c_args.addAll(0, tmp.args);
            } else {
                //如果大于0
                String idxBef = c_builder.substring(0, offset);
                int idxBefQm = 0;
                for (int i = 0; i < idxBef.length(); i++) {
                    if (idxBef.charAt(i) == '?') {
                        idxBefQm++;
                    }
                }

                c_builder.insert(offset, tmp.sql);
                c_args.addAll(idxBefQm, tmp.args);
            }
        }

        return this;
    }

    /**
     * 插入到最前面
     */
    public SqlBuilder insert(String sql, Object... args) {
        return insert(0, sql, args);
    }

    /**
     * 插入SQL部分
     */
    public SqlBuilder insert(SqlBuilder part) {
        c_builder.insert(0, part.c_builder);
        c_args.addAll(0, part.c_args);
        return this;
    }

    /**
     * 添加SQL
     */
    public SqlBuilder appendIf(boolean condition, String sql, Object... args) {
        if (condition) {
            if (Utils.isEmpty(args)) {
                c_builder.append(sql);
            } else {
                SqlPartBuilder pb = new SqlPartBuilder(sql, args);

                c_builder.append(pb.sql);
                c_args.addAll(pb.args);
            }
        }

        return this;
    }

    /**
     * 添加SQL
     */
    public SqlBuilder append(String sql, Object... args) {
        return appendIf(true, sql, args);
    }

    /**
     * 添加SQL部分
     */
    public SqlBuilder append(SqlBuilder sqlPart) {
        c_builder.append(sqlPart.c_builder);
        c_args.addAll(sqlPart.c_args);
        return this;
    }

    /**
     * 移除字符
     */
    public SqlBuilder remove(int start, int length) {
        c_builder.delete(start, start + length);
        return this;
    }

    /**
     * 移除最后字符
     */
    public SqlBuilder removeLast() {
        c_builder.setLength(c_builder.length() - 1);
        return this;
    }

    /**
     * 修剪未尾
     */
    public SqlBuilder trimEnd(String str) {
        int len = str.length();
        if (len > 0) {
            String tmp = c_builder.toString().trim();

            while (true) {
                int idx = tmp.lastIndexOf(str);
                if (idx == tmp.length() - len) {
                    tmp = tmp.substring(0, tmp.length() - len);
                } else {
                    break;
                }
            }
            c_builder.setLength(0);
            c_builder.append(tmp);
        }

        return this;
    }


    /**
     * 修剪开头
     */
    public SqlBuilder trimStart(String str) {
        int len = str.length();
        if (len > 0) {
            String tmp = c_builder.toString().trim();

            while (true) {
                int idx = tmp.indexOf(str);
                if (idx == 0) {
                    tmp = tmp.substring(len);
                } else {
                    break;
                }
            }
            c_builder.setLength(0);
            c_builder.append(tmp);
        }
        return this;
    }

    /**
     * 获取代码
     */
    @Override
    public String getSql() {
        return c_builder.toString();
    }

    /**
     * 获取参数
     */
    @Override
    public Object[] getArgs() {
        return c_args.toArray();
    }

    @Override
    public String toString() {
        return c_builder.toString();
    }

    /**
     * 部分构建
     */
    static class SqlPartBuilder {
        public CharSequence sql;
        public List<Object> args;

        public SqlPartBuilder(CharSequence sql, Object[] args) {
            this.args = new ArrayList<>();

            if (args != null && args.length > 0) {
                StringBuilder buf = new StringBuilder();
                buf.append(sql);

                for (Object a1 : args) {
                    if (a1 instanceof Iterable) { //将数组转为单体
                        int idx = buf.indexOf("?...");
                        if (idx < 0) {
                            throw new IllegalArgumentException("Arg iterable required symbol '?...'");
                        }

                        StringBuilder tmp = new StringBuilder();
                        for (Object a2 : (Iterable) a1) {
                            this.args.add(a2);
                            tmp.append("?").append(",");
                        }

                        if (tmp.length() > 0) {
                            tmp.setLength(tmp.length() - 1);
                        }

                        if (tmp.length() == 0) {
                            buf.replace(idx, idx + 4, "null");
                        } else {
                            buf.replace(idx, idx + 4, tmp.toString());
                        }
                    } else {
                        this.args.add(a1);
                    }
                }

                this.sql = buf;
            } else {
                this.sql = sql;
            }
        }
    }
}