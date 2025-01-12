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

/**
 * 序列化
 *
 * @author songyinyin
 * @since 2.2
 */
public class SerializationHint {

    private String name;

    private String reachableType;

    private String customTargetConstructorClass;

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

    public String getCustomTargetConstructorClass() {
        return customTargetConstructorClass;
    }

    public void setCustomTargetConstructorClass(String customTargetConstructorClass) {
        this.customTargetConstructorClass = customTargetConstructorClass;
    }

}
