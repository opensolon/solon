package org.noear.solon.gradle.plugin;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.gradle.dsl.SolonExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SolonPlugin implements Plugin<Project> {

    public static final String SOLON_JAR_TASK_NAME = "solonJar";
    public static final String SOLON_WAR_TASK_NAME = "solonWar";

    public static final String SOLON_EXT_NAME = "solon";

    @Override
    public void apply(@NotNull Project project) {
        verifyGradleVersion();
        createExtension(project);
        registerPluginActions(project);
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

    private void registerPluginActions(Project project) {

        List<PluginApplicationAction> actions = Arrays.asList(new JavaPluginAction(), new WarPluginAction());

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
