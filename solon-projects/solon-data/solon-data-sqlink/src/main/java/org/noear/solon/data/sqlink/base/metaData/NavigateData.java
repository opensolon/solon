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
package org.noear.solon.data.sqlink.base.metaData;

import org.noear.solon.data.sqlink.base.annotation.Navigate;
import org.noear.solon.data.sqlink.base.annotation.RelationType;

import java.util.Collection;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class NavigateData {
    private final Navigate navigate;
    private final Class<?> navigateTargetType;
    private final Class<? extends Collection<?>> navigateCollectionType;

    public NavigateData(Navigate navigate, Class<?> navigateTargetType, Class<? extends Collection<?>> navigateCollectionType) {
        this.navigate = navigate;
        this.navigateTargetType = navigateTargetType;
        this.navigateCollectionType = navigateCollectionType;
    }

    public String getSelfPropertyName() {
        return navigate.self();
    }

    public String getTargetPropertyName() {
        return navigate.target();
    }

    public RelationType getRelationType() {
        return navigate.value();
    }

    public Class<?> getNavigateTargetType() {
        return navigateTargetType;
    }

    public boolean isCollectionWrapper() {
        return navigateCollectionType != null;
    }

    public Class<? extends Collection<?>> getCollectionWrapperType() {
        return navigateCollectionType;
    }

    public Class<? extends IMappingTable> getMappingTableType() {
        return navigate.mappingTable();
    }

    public String getSelfMappingPropertyName() {
        return navigate.selfMapping();
    }

    public String getTargetMappingPropertyName() {
        return navigate.targetMapping();
    }
}
