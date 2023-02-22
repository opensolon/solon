package org.noear.solon.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.*;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.gradle.dsl.SolonExtension;
import org.noear.solon.gradle.tasks.bundling.SolonJar;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

final class JavaPluginAction implements PluginApplicationAction {

    private static final String PARAMETERS_COMPILER_ARG = "-parameters";

    private final SinglePublishedArtifact singlePublishedArtifact;

    JavaPluginAction(SinglePublishedArtifact artifact) {
        this.singlePublishedArtifact = artifact;
    }

    @Override
    public Class<? extends Plugin<? extends Project>> getPluginClass() {
        return JavaPlugin.class;
    }

    @Override
    public void execute(@NotNull Project project) {
        classifyJarTask(project);
        configureBuildTask(project);
        TaskProvider<ResolveMainClassName> resolveMainClassName = configureResolveMainClassNameTask(project);
        TaskProvider<SolonJar> solonJar = configureSolonJarTask(project, resolveMainClassName);
        configureArtifactPublication(solonJar);
        project.afterEvaluate(this::configureUtf8Encoding);
        configureParametersCompilerArg(project);
    }

    private void configureBuildTask(Project project) {
        project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME)
                .configure((task) -> task.dependsOn(this.singlePublishedArtifact));
    }

    private TaskProvider<ResolveMainClassName> configureResolveMainClassNameTask(Project project) {
        return project.getTasks().register(SolonPlugin.RESOLVE_MAIN_CLASS_NAME_TASK_NAME,
                ResolveMainClassName.class, (resolveMainClassName) -> {
                    ExtensionContainer extensions = project.getExtensions();
                    resolveMainClassName.setDescription("Resolves the name of the application's main class.");
                    resolveMainClassName.setGroup(BasePlugin.BUILD_GROUP);
                    Callable<FileCollection> classpath = () -> project.getExtensions()
                            .getByType(SourceSetContainer.class).getByName(SourceSet.MAIN_SOURCE_SET_NAME).getOutput();
                    resolveMainClassName.setClasspath(classpath);
                    resolveMainClassName.getConfiguredMainClassName().convention(project.provider(() -> {

                        String javaApplicationMainClass = getJavaApplicationMainClass(extensions);

                        if (javaApplicationMainClass != null) {
                            return javaApplicationMainClass;
                        }

                        SolonExtension springBootExtension = project.getExtensions()
                                .findByType(SolonExtension.class);

                        return Objects.requireNonNull(springBootExtension).getMainClass().getOrNull();
                    }));
                    resolveMainClassName.getOutputFile()
                            .set(project.getLayout().getBuildDirectory().file("resolvedMainClassName"));
                });
    }

    private static String getJavaApplicationMainClass(ExtensionContainer extensions) {
        JavaApplication javaApplication = extensions.findByType(JavaApplication.class);
        if (javaApplication == null) {
            return null;
        }
        return javaApplication.getMainClass().getOrNull();
    }

    private JavaPluginExtension javaPluginExtension(Project project) {
        return project.getExtensions().getByType(JavaPluginExtension.class);
    }

    private void classifyJarTask(Project project) {
        project.getTasks().named(JavaPlugin.JAR_TASK_NAME, Jar.class)
                .configure((task) -> task.getArchiveClassifier().convention("plain"));
    }

    private void configureArtifactPublication(TaskProvider<SolonJar> bootJar) {
        this.singlePublishedArtifact.addJarCandidate(bootJar);
    }

    private void configureUtf8Encoding(Project evaluatedProject) {
        evaluatedProject.getTasks().withType(JavaCompile.class).configureEach(this::configureUtf8Encoding);
    }

    private void configureUtf8Encoding(JavaCompile compile) {
        CompileOptions options = compile.getOptions();
        if (options.getEncoding() == null) {
            options.setEncoding("UTF-8");
        }
    }

    private void configureParametersCompilerArg(Project project) {
        project.getTasks().withType(JavaCompile.class).configureEach((compile) -> {
            List<String> compilerArgs = compile.getOptions().getCompilerArgs();
            if (!compilerArgs.contains(PARAMETERS_COMPILER_ARG)) {
                compilerArgs.add(PARAMETERS_COMPILER_ARG);
            }
        });
    }

    private TaskProvider<SolonJar> configureSolonJarTask(Project project, TaskProvider<ResolveMainClassName> resolveMainClassName) {
        SourceSet mainSourceSet = javaPluginExtension(project).getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        Callable<List<Object>> classpath = () -> mainSourceSet.getRuntimeClasspath().getFiles().stream().map(file -> file.isDirectory() ? file : project.zipTree(file)).collect(Collectors.toList());

        return project.getTasks().register(SolonPlugin.SOLON_JAR_TASK_NAME, SolonJar.class, (bootJar) -> {
            bootJar.setDescription("Assembles an executable jar archive containing the main classes and their dependencies.");
            bootJar.setGroup(BasePlugin.BUILD_GROUP);
            bootJar.classpath(classpath);

            bootJar.getMainClass().convention(resolveMainClassName.flatMap((resolver) -> resolveMainClassName.get().readMainClassName()));

            bootJar.getTargetJavaVersion()
                    .set(project.provider(() -> javaPluginExtension(project).getTargetCompatibility()));
        });
    }
}
