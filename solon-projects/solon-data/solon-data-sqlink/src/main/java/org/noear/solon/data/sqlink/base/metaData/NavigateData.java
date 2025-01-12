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
package org.noear.solon.data.sqlink.base.metaData;

import org.noear.solon.data.sqlink.annotation.Navigate;
import org.noear.solon.data.sqlink.annotation.RelationType;

import java.util.Collection;

/**
 * 导航数据
 *
 * @author kiryu1223
 * @since 3.0
 */
public class NavigateData {
    /**
     * 导航注解
     */
    private final Navigate navigate;
    /**
     * 导航目标类型
     */
    private final Class<?> navigateTargetType;
    /**
     * 导航目标集合类型
     */
    private final Class<? extends Collection<?>> navigateCollectionType;

    public NavigateData(Navigate navigate, Class<?> navigateTargetType, Class<? extends Collection<?>> navigateCollectionType) {
        this.navigate = navigate;
        this.navigateTargetType = navigateTargetType;
        this.navigateCollectionType = navigateCollectionType;
    }


    /**
     * 自己的字段名
     */
    public String getSelfFieldName() {
        return navigate.self();
    }

    /**
     * 目标字段名
     */
    public String getTargetFieldName() {
        return navigate.target();
    }

    /**
     * 关系类型
     */
    public RelationType getRelationType() {
        return navigate.value();
    }

    /**
     * 导航目标类型
     */
    public Class<?> getNavigateTargetType() {
        return navigateTargetType;
    }

    /**
     * 是否是集合
     */
    public boolean isCollectionWrapper() {
        return navigateCollectionType != null;
    }

    /**
     * 集合类型
     */
    public Class<? extends Collection<?>> getCollectionWrapperType() {
        return navigateCollectionType;
    }

    /**
     * 中间表的类型
     */
    public Class<? extends IMappingTable> getMappingTableType() {
        return navigate.mappingTable();
    }

    /**
     * 自身对应中间表的字段名
     */
    public String getSelfMappingFieldName() {
        return navigate.selfMapping();
    }

    /**
     * 目标对应中间表的字段名
     */
    public String getTargetMappingFieldName() {
        return navigate.targetMapping();
    }
}
