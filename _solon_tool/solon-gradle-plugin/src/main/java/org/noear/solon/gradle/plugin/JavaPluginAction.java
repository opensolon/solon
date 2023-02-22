package org.noear.solon.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.gradle.tasks.bundling.SolonJar;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

final class JavaPluginAction implements PluginApplicationAction {

    private static final String PARAMETERS_COMPILER_ARG = "-parameters";

    @Override
    public Class<? extends Plugin<? extends Project>> getPluginClass() {
        return JavaPlugin.class;
    }

    @Override
    public void execute(@NotNull Project project) {
        classifyJarTask(project);

        configureSolonJarTask(project);

        project.afterEvaluate(this::configureUtf8Encoding);
        configureParametersCompilerArg(project);
    }

    private void classifyJarTask(Project project) {
        project.getTasks().named(JavaPlugin.JAR_TASK_NAME, Jar.class)
                .configure((task) -> task.getArchiveClassifier().convention("plain"));
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

    private void configureSolonJarTask(Project project) {
        SourceSet mainSourceSet = PluginApplicationAction.javaPluginExtension(project).getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        Callable<List<Object>> classpath = () -> mainSourceSet.getRuntimeClasspath().getFiles().stream().map(file -> file.isDirectory() ? file : project.zipTree(file)).collect(Collectors.toList());

        project.getTasks().register(SolonPlugin.SOLON_JAR_TASK_NAME, SolonJar.class, (bootJar) -> {
            bootJar.setDescription("Assembles an executable jar archive containing the main classes and their dependencies.");
            bootJar.setGroup(BasePlugin.BUILD_GROUP);
            bootJar.classpath(classpath);
            bootJar.getMainClass().convention(PluginApplicationAction.configureResolveMainClassName(project));

            bootJar.getTargetJavaVersion()
                    .set(project.provider(() -> PluginApplicationAction.javaPluginExtension(project).getTargetCompatibility()));
        });
    }
}
