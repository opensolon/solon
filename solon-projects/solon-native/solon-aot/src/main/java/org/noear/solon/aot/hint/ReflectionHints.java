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
package org.noear.solon.aot.hint;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 反射的提示
 *
 * @author songyinyin
 * @since 2.2
 */
public class ReflectionHints {

    private String name;

    private String reachableType;

    private Set<String> fields = new LinkedHashSet<>();

    private Set<ExecutableHint> methods = new LinkedHashSet<>();

    private Set<ExecutableHint> constructors = new LinkedHashSet<>();

    private Set<MemberCategory> memberCategories = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReachableType() {
        return reachableType;
    }

    public void setReachableType(String reachableType) {
        this.reachableType = reachableType;
    }

    public Set<String> getFields() {
        return fields;
    }

    public Set<ExecutableHint> getMethods() {
        return methods;
    }

    public Set<ExecutableHint> getConstructors() {
        return constructors;
    }

    public Set<MemberCategory> getMemberCategories() {
        return memberCategories;
    }
}
