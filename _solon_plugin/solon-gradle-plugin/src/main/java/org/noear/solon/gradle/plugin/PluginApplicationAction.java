/*
 * Copyright 2012-2020 the original author or authors.
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

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.noear.solon.gradle.dsl.SolonExtension;

/**
 * An {@link Action} to be executed on a {@link Project} in response to a particular type
 * of {@link Plugin} being applied.
 *
 * @author Andy Wilkinson
 */
interface PluginApplicationAction extends Action<Project> {

    /**
     * The class of the {@code Plugin} that, when applied, will trigger the execution of
     * this action.
     *
     * @return the plugin class
     * @throws ClassNotFoundException if the plugin class cannot be found
     * @throws NoClassDefFoundError   if an error occurs when defining the plugin class
     */
    Class<? extends Plugin<? extends Project>> getPluginClass() throws ClassNotFoundException, NoClassDefFoundError;

    static String configureResolveMainClassName(Project project) {

        SolonExtension extension = project.getExtensions()
                .findByType(SolonExtension.class);

        String mainClassName = extension == null ? null : extension.getMainClass().getOrNull();

        if (mainClassName == null || mainClassName.isEmpty()) {
            // 也可以配置在 Application 里面
            return getJavaApplicationMainClass(project.getExtensions());
        }

        return mainClassName;
    }

    static JavaPluginExtension javaPluginExtension(Project project) {
        return project.getExtensions().getByType(JavaPluginExtension.class);
    }

    static String getJavaApplicationMainClass(ExtensionContainer extensions) {
        JavaApplication javaApplication = extensions.findByType(JavaApplication.class);
        if (javaApplication == null) {
            return null;
        }

        return javaApplication.getMainClass().getOrNull();
    }
}
