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
package org.noear.solon.gradle.tasks.bundling;

import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.bundling.Jar;

import java.util.Collections;

public abstract class SolonJar extends Jar implements SolonArchive {
    private final SolonArchiveSupport support;

    private final Provider<String> projectName;

    private final Provider<Object> projectVersion;

    private FileCollection classpath;

    public SolonJar() {
        this.support = new SolonArchiveSupport();
        setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);

        Project project = getProject();
        this.projectName = project.provider(project::getName);
        this.projectVersion = project.provider(project::getVersion);
    }

    @Override
    public void copy() {
        this.support.configureManifest(
                getManifest(),
                getMainClass().get(),
                this.getTargetJavaVersion().get().toString(),
                this.projectName.get(),
                this.projectVersion.get()
        );

        from(getClasspath());
        super.copy();
    }

    @Override
    public FileCollection getClasspath() {
        return this.classpath;
    }

    @Override
    public void classpath(Object... classpath) {
        FileCollection existingClasspath = this.classpath;
        this.classpath = getProject().files((existingClasspath != null) ? existingClasspath : Collections.emptyList(), classpath);
    }

    @Override
    public void setClasspath(Object classpath) {
        this.classpath = getProject().files(classpath);
    }

    @Override
    public void setClasspath(FileCollection classpath) {
        this.classpath = getProject().files(classpath);
    }
}
