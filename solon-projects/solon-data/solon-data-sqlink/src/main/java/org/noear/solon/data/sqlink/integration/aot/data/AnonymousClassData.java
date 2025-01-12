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
 * 匿名类数据记录（请勿使用）
 *
 * @author kiryu1223
 * @since 3.0
 */
public class AnonymousClassData extends ClassData {
    /**
     * 匿名类名称
     */
    private String name;
    /**
     * 是否使用unsafe创建
     */
    private boolean unsafeAllocated = true;
    /**
     * 所有私有字段
     */
    private boolean allDeclaredFields = true;
    /**
     * 所有公开方法
     */
    private boolean allPublicMethods = true;

    public AnonymousClassData(String name) {
        super(name);
        this.name = name;
    }

    /**
     * 匿名类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置匿名类名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 是否使用unsafe创建
     */
    public boolean isUnsafeAllocated() {
        return unsafeAllocated;
    }

    /**
     * 设置是否使用unsafe创建
     */
    public void setUnsafeAllocated(boolean unsafeAllocated) {
        this.unsafeAllocated = unsafeAllocated;
    }

    /**
     * 是否使用所有字段
     */
    public boolean isAllDeclaredFields() {
        return allDeclaredFields;
    }

    /**
     * 设置是否使用所有字段
     */
    public void setAllDeclaredFields(boolean allDeclaredFields) {
        this.allDeclaredFields = allDeclaredFields;
    }

    /**
     * 是否使用所有方法
     */
    public boolean isAllPublicMethods() {
        return allPublicMethods;
    }

    /**
     * 设置是否使用所有方法
     */
    public void setAllPublicMethods(boolean allPublicMethods) {
        this.allPublicMethods = allPublicMethods;
    }
}
