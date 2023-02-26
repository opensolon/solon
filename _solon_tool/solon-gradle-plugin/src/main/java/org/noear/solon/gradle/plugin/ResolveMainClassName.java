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

package org.noear.solon.gradle.plugin;

import org.gradle.api.*;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.work.DisableCachingByDefault;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.gradle.tools.MainClassFinder;
import org.noear.solon.gradle.util.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * {@link Task} for resolving the name of the application's main class.
 *
 * @author Andy Wilkinson
 * @since 2.4
 */
@DisableCachingByDefault(because = "Not worth caching")
public class ResolveMainClassName extends DefaultTask {

    private static final String SOLON_APPLICATION_CLASS_NAME = "org.noear.solon.annotation.SolonMain";


    private final RegularFileProperty outputFile;

    private final Property<String> configuredMainClass;

    private FileCollection classpath;

    /**
     * Creates a new instance of the {@code ResolveMainClassName} task.
     */
    public ResolveMainClassName() {
        this.outputFile = getProject().getObjects().fileProperty();
        this.configuredMainClass = getProject().getObjects().property(String.class);
    }

    /**
     * Returns the classpath that the task will examine when resolving the main class
     * name.
     *
     * @return the classpath
     */
    @Classpath
    public FileCollection getClasspath() {
        return this.classpath;
    }

    /**
     * Sets the classpath that the task will examine when resolving the main class name.
     *
     * @param classpath the classpath
     */
    public void setClasspath(FileCollection classpath) {
        setClasspath((Object) classpath);
    }

    /**
     * Sets the classpath that the task will examine when resolving the main class name.
     * The given {@code classpath} is evaluated as per {@link Project#files(Object...)}.
     *
     * @param classpath the classpath
     * @since 2.5.10
     */
    public void setClasspath(Object classpath) {
        this.classpath = getProject().files(classpath);
    }

    /**
     * Returns the property for the task's output file that will contain the name of the
     * main class.
     *
     * @return the output file
     */
    @OutputFile
    public RegularFileProperty getOutputFile() {
        return this.outputFile;
    }

    /**
     * Returns the property for the explicitly configured main class name that should be
     * used in favor of resolving the main class name from the classpath.
     *
     * @return the configured main class name property
     */
    @Input
    @Optional
    public Property<String> getConfiguredMainClassName() {
        return this.configuredMainClass;
    }

    @TaskAction
    void resolveAndStoreMainClassName() throws IOException {
        File outputFile = this.outputFile.getAsFile().get();
        outputFile.getParentFile().mkdirs();
        String mainClassName = resolveMainClassName();

        IoUtils.writeString(outputFile, mainClassName);
    }

    private String resolveMainClassName() {
        String configuredMainClass = this.configuredMainClass.getOrNull();
        if (configuredMainClass != null) {
            return configuredMainClass;
        }
        return getClasspath().filter(File::isDirectory).getFiles().stream().map(this::findMainClass)
                .filter(Objects::nonNull).findFirst().orElse("");
    }

    private String findMainClass(File file) {
        try {
            return MainClassFinder.findSingleMainClass(file, SOLON_APPLICATION_CLASS_NAME);
        } catch (IOException ex) {
            return null;
        }
    }

    Provider<String> readMainClassName() {
        return this.outputFile.map(new ClassNameReader());
    }

    private static final class ClassNameReader implements Transformer<String, RegularFile> {

        @NotNull
        @Override
        public String transform(RegularFile file) {
            if (file.getAsFile().length() == 0) {
                throw new InvalidUserDataException("Main class name has not been configured and it could not be resolved");
            }
            File output = file.getAsFile();
            try {
                return IoUtils.readString(output);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to read main class name from '" + output + "'");
            }
        }

    }

}
