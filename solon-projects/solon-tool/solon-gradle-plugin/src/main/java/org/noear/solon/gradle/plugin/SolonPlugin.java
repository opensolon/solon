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
package org.noear.solon.gradle.plugin;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.gradle.dsl.SolonExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SolonPlugin implements Plugin<Project> {

    public static final String SOLON_JAR_TASK_NAME = "solonJar";
    public static final String SOLON_WAR_TASK_NAME = "solonWar";

    public static final String SOLON_ARCHIVES_CONFIGURATION_NAME = "solonArchives";

    /**
     * The name of the {@link ResolveMainClassName} task.
     */
    public static final String RESOLVE_MAIN_CLASS_NAME_TASK_NAME = "resolveMainClassName";
    public static final String SOLON_EXT_NAME = "solon";

    @Override
    public void apply(@NotNull Project project) {
        verifyGradleVersion();
        createExtension(project);

        Configuration configuration = createBootArchivesConfiguration(project);
        registerPluginActions(project, configuration);
    }

    private void verifyGradleVersion() {
        GradleVersion currentVersion = GradleVersion.current();
        if (currentVersion.compareTo(GradleVersion.version("7.0")) < 0) {
            throw new GradleException("Solon plugin requires Gradle 7.x (7.0 or later). "
                    + "The current version is " + currentVersion);
        }
    }

    private void createExtension(Project project) {
        project.getExtensions().create(SOLON_EXT_NAME, SolonExtension.class, project);
    }

    private Configuration createBootArchivesConfiguration(Project project) {
        Configuration bootArchives = project.getConfigurations().create(SOLON_ARCHIVES_CONFIGURATION_NAME);
        bootArchives.setDescription("Configuration for Solon archive artifacts.");
        bootArchives.setCanBeResolved(false);
        return bootArchives;
    }

    private void registerPluginActions(Project project, Configuration configuration) {
        SinglePublishedArtifact artifact = new SinglePublishedArtifact(configuration, project.getArtifacts());

        List<PluginApplicationAction> actions = Arrays.asList(new JavaPluginAction(artifact), new WarPluginAction(artifact), new KotlinPluginAction());

        for (PluginApplicationAction action : actions) {
            withPluginClassOfAction(action, (pluginClass) -> project.getPlugins().withType(pluginClass, (plugin) -> action.execute(project)));
        }
    }

    private void withPluginClassOfAction(PluginApplicationAction action,
                                         Consumer<Class<? extends Plugin<? extends Project>>> consumer) {
        Class<? extends Plugin<? extends Project>> pluginClass;
        try {
            pluginClass = action.getPluginClass();
        } catch (Throwable ex) {
            // Plugin class unavailable.
            return;
        }
        consumer.accept(pluginClass);
    }
}
