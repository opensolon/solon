package org.noear.solon.gradle.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.War;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.gradle.tasks.bundling.SolonWar;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * {@link Action} that is executed in response to the {@link WarPlugin} being applied.
 *
 * @author Andy Wilkinson
 * @author Scott Frederick
 */
class WarPluginAction implements PluginApplicationAction {

    private final SinglePublishedArtifact singlePublishedArtifact;

    WarPluginAction(SinglePublishedArtifact artifact) {
        this.singlePublishedArtifact = artifact;
    }

    @Override
    public Class<? extends Plugin<? extends Project>> getPluginClass() {
        return WarPlugin.class;
    }

    @Override
    public void execute(@NotNull Project project) {
        classifyWarTask(project);
        TaskProvider<SolonWar> bootWar = configureSolonWarTask(project);
        configureArtifactPublication(bootWar);
    }

    private void classifyWarTask(Project project) {
        project.getTasks().named(WarPlugin.WAR_TASK_NAME, War.class)
                .configure((war) -> war.getArchiveClassifier().convention("plain"));
    }

    private TaskProvider<SolonWar> configureSolonWarTask(Project project) {
        SourceSet mainSourceSet = project.getExtensions().getByType(SourceSetContainer.class)
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        Callable<FileCollection> classpath = mainSourceSet::getRuntimeClasspath;
        TaskProvider<ResolveMainClassName> resolveMainClassName = project.getTasks()
                .named(SolonPlugin.RESOLVE_MAIN_CLASS_NAME_TASK_NAME, ResolveMainClassName.class);

        TaskProvider<SolonWar> bootWarProvider = project.getTasks().register(SolonPlugin.SOLON_WAR_TASK_NAME,
                SolonWar.class, (bootWar) -> {
                    bootWar.setGroup(BasePlugin.BUILD_GROUP);
                    bootWar.setDescription("Assembles an executable war archive containing webapp"
                            + " content, and the main classes and their dependencies.");
                    bootWar.providedClasspath(providedRuntimeConfiguration(project));
                    bootWar.setClasspath(classpath);

                    bootWar.getMainClass()
                            .convention(resolveMainClassName.flatMap((resolver) -> resolveMainClassName.get().readMainClassName()));

                    bootWar.getTargetJavaVersion()
                            .set(project.provider(() -> javaPluginExtension(project).getTargetCompatibility()));
                });

        bootWarProvider.map((Transformer<Object, SolonWar>) war -> Objects.requireNonNull(war.getClasspath()));

        return bootWarProvider;
    }

    private FileCollection providedRuntimeConfiguration(Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        return configurations.getByName(WarPlugin.PROVIDED_RUNTIME_CONFIGURATION_NAME);
    }

    private void configureArtifactPublication(TaskProvider<SolonWar> bootWar) {
        this.singlePublishedArtifact.addWarCandidate(bootWar);
    }

    private JavaPluginExtension javaPluginExtension(Project project) {
        return project.getExtensions().getByType(JavaPluginExtension.class);
    }

}
