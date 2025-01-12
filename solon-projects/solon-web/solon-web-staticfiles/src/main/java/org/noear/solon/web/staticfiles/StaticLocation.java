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
package org.noear.solon.web.staticfiles;

/**
 * 静态文件位置
 *
 * @author noear
 * @since 1.0
 * */
public class StaticLocation {
    /**
     * 路径前缀
     */
    public final String pathPrefix;
    /**
     * 路径前缀是文件
     */
    public final boolean pathPrefixAsFile;
    /**
     * 资源仓库
     */
    public final StaticRepository repository;

    /**
     * 资源仓库是否包括路径前缀（默认为：true）
     *
     * @since 1.6
     */
    public final boolean repositoryIncPrefix;

    public StaticLocation(String pathPrefix, StaticRepository repository, boolean repositoryIncPrefix) {
        this.pathPrefix = pathPrefix;
        this.pathPrefixAsFile = (pathPrefix.endsWith("/") == false);
        this.repository = repository;
        this.repositoryIncPrefix = repositoryIncPrefix;
    }
}
