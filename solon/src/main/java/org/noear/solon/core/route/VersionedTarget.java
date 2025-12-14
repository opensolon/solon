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
package org.noear.solon.core.route;

/**
 * 有版本的目标
 *
 * @author noear
 * @since 3.7
 */
public class VersionedTarget<T> {
    private final Version version;
    private final T target;

    public VersionedTarget(Version version, T target) {
        this.version = (version == null ? Version.EMPTY : version);
        this.target = target;
    }

    public T getTarget() {
        return target;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return version.getOriginal();
    }
}