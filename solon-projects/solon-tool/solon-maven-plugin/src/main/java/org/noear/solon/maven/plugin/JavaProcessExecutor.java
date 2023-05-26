package org.noear.solon.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.toolchain.ToolchainManager;
import org.noear.solon.maven.plugin.tools.SolonMavenUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Ease the execution of a Java process using Maven's toolchain support.
 *
 * @author Stephane Nicoll
 */
class JavaProcessExecutor {

    private static final int EXIT_CODE_SIGINT = 130;

    private final MavenSession mavenSession;

    private final ToolchainManager toolchainManager;

    private final Consumer<RunProcess> runProcessCustomizer;

    JavaProcessExecutor(MavenSession mavenSession, ToolchainManager toolchainManager) {
        this(mavenSession, toolchainManager, null);
    }

    private JavaProcessExecutor(MavenSession mavenSession, ToolchainManager toolchainManager,
                                Consumer<RunProcess> runProcessCustomizer) {
        this.mavenSession = mavenSession;
        this.toolchainManager = toolchainManager;
        this.runProcessCustomizer = runProcessCustomizer;
    }

    JavaProcessExecutor withRunProcessCustomizer(Consumer<RunProcess> customizer) {
        Consumer<RunProcess> combinedCustomizer = (this.runProcessCustomizer != null)
                ? this.runProcessCustomizer.andThen(customizer) : customizer;
        return new JavaProcessExecutor(this.mavenSession, this.toolchainManager, combinedCustomizer);
    }

    int run(File workingDirectory, List<String> args, Map<String, String> environmentVariables)
            throws MojoExecutionException {
        RunProcess runProcess = new RunProcess(workingDirectory, getJavaExecutable());
        if (this.runProcessCustomizer != null) {
            this.runProcessCustomizer.accept(runProcess);
        }
        try {
            int exitCode = runProcess.run(true, args, environmentVariables);
            if (!hasTerminatedSuccessfully(exitCode)) {
                throw new MojoExecutionException("Process terminated with exit code: " + exitCode);
            }
            return exitCode;
        } catch (IOException ex) {
            throw new MojoExecutionException("Process execution failed", ex);
        }
    }

    RunProcess runAsync(File workingDirectory, List<String> args, Map<String, String> environmentVariables)
            throws MojoExecutionException {
        try {
            RunProcess runProcess = new RunProcess(workingDirectory, getJavaExecutable());
            runProcess.run(false, args, environmentVariables);
            return runProcess;
        } catch (IOException ex) {
            throw new MojoExecutionException("Process execution failed", ex);
        }
    }

    private boolean hasTerminatedSuccessfully(int exitCode) {
        return (exitCode == 0 || exitCode == EXIT_CODE_SIGINT);
    }

    private String getJavaExecutable() {
        return SolonMavenUtil.getJavaExecutable(toolchainManager, mavenSession);
    }

}
