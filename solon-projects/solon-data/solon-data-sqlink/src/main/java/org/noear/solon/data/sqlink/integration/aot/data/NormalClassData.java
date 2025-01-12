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
package org.noear.solon.data.sqlink.integration.aot.data;

/**
 * 普通类数据记录（请勿使用）
 *
 * @author kiryu1223
 * @since 3.0
 */
public class NormalClassData extends ClassData {
    /**
     * 类名称
     */
    private String name;
    /**
     * 所有公开构造函数
     */
    protected boolean allPublicConstructors = true;
    /**
     * 所有私有字段
     */
    protected boolean allDeclaredFields = true;
    /**
     * 所有公开方法
     */
    protected boolean allPublicMethods = true;

    public NormalClassData(String name) {
        super(name);
        this.name = name;
    }

    /**
     * 获取类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置类名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 是否所有公开构造函数
     */
    public boolean isAllPublicConstructors() {
        return allPublicConstructors;
    }

    /**
     * 设置是否所有公开构造函数
     */
    public void setAllPublicConstructors(boolean allPublicConstructors) {
        this.allPublicConstructors = allPublicConstructors;
    }

    /**
     * 是否所有私有字段
     */
    public boolean isAllDeclaredFields() {
        return allDeclaredFields;
    }

    /**
     * 设置是否所有私有字段
     */
    public void setAllDeclaredFields(boolean allDeclaredFields) {
        this.allDeclaredFields = allDeclaredFields;
    }

    /**
     * 是否所有公开方法
     */
    public boolean isAllPublicMethods() {
        return allPublicMethods;
    }

    /**
     * 设置是否所有公开方法
     */
    public void setAllPublicMethods(boolean allPublicMethods) {
        this.allPublicMethods = allPublicMethods;
    }
}
