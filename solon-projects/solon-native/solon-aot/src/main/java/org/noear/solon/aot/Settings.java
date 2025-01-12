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
package org.noear.solon.aot;

import java.nio.file.Path;

/**
 * @author noear
 * @see 2.2
 */
public final class Settings {

    private Path classOutput;

    /**
     * aot生成的Java类
     */
    private Path generatedSources;

    private String groupId;

    private String artifactId;

    private String nativeBuildArgs;

    public Settings(Path classOutput, Path generatedSources, String groupId, String artifactId, String nativeBuildArgs) {
        this.classOutput = classOutput;
        this.generatedSources = generatedSources;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.nativeBuildArgs = nativeBuildArgs;
    }

    public Path getClassOutput() {
        return classOutput;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public Path getGeneratedSources() {
        return generatedSources;
    }

    public String getNativeBuildArgs() {
        return nativeBuildArgs;
    }
}