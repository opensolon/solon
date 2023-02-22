/*
 * Copyright 2012-2022 the original author or authors.
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

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

/**
 * A Spring Boot "fat" archive task.
 *
 * @author Andy Wilkinson
 */
public interface SolonArchive extends Task {

    /**
     * Returns the fully-qualified name of the application's main class.
     *
     * @return the fully-qualified name of the application's main class
     */
    @Input
    Property<String> getMainClass();


    /**
     * Returns the classpath that will be included in the archive.
     *
     * @return the classpath
     */
    @Optional
    @Classpath
    FileCollection getClasspath();

    /**
     * Adds files to the classpath to include in the archive. The given {@code classpath}
     * is evaluated as per {@link Project#files(Object...)}.
     *
     * @param classpath the additions to the classpath
     */
    void classpath(Object... classpath);

    /**
     * Sets the classpath to include in the archive. The given {@code classpath} is
     * evaluated as per {@link Project#files(Object...)}.
     *
     * @param classpath the classpath
     */
    void setClasspath(Object classpath);

    /**
     * Sets the classpath to include in the archive.
     *
     * @param classpath the classpath
     */
    void setClasspath(FileCollection classpath);

    /**
     * Returns the target Java version of the project (e.g. as provided by the
     * {@code targetCompatibility} build property).
     *
     * @return the target Java version
     */
    @Input
    @Optional
    Property<JavaVersion> getTargetJavaVersion();

}
