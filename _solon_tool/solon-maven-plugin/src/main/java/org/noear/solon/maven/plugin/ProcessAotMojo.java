package org.noear.solon.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.toolchain.ToolchainManager;
import org.noear.solon.maven.plugin.tools.SolonMavenUtil;
import org.noear.solon.maven.plugin.tools.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Invoke the AOT engine on the application.
 *
 * @author songyinyin
 * @since 2023/4/8 21:03
 */
@Mojo(name = "process-aot", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ProcessAotMojo extends AbstractMojo {

    private static final String AOT_PROCESSOR_CLASS_NAME = "org.noear.solon.aot.SolonAotProcessor";

    /**
     * The current Maven session. This is used for toolchain manager API calls.
     */
    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    /**
     * The toolchain manager to use to locate a custom JDK.
     */
    @Component
    private ToolchainManager toolchainManager;

    @Component
    private MavenProject project;

    /**
     * Directory containing the classes and resource files that should be packaged into
     * the archive.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File classesDirectory;

    /**
     * Name of the main class to use as the source for the AOT process. If not specified
     * the first compiled class found that contains a 'main' method will be used.
     */
    @Parameter(property = "solon.aot.main-class")
    private String mainClass;

    /**
     * List of JVM system properties to pass to the AOT process.
     */
    @Parameter
    private Map<String, String> systemPropertyVariables;

    /**
     * JVM arguments that should be associated with the AOT process. On command line, make
     * sure to wrap multiple values between quotes.
     */
    @Parameter(property = "solon.aot.jvmArguments")
    private String jvmArguments;

    /**
     * Arguments that should be provided to the AOT compile process. On command line, make
     * sure to wrap multiple values between quotes.
     */
    @Parameter(property = "solon.aot.compilerArguments")
    private String compilerArguments;

    /**
     * Application arguments that should be taken into account for AOT processing.
     */
    @Parameter
    private String[] arguments;

    /**
     * solon envs to take into account for AOT processing.
     */
    @Parameter
    private String[] envs;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("start process aot ...");

        try {
            String applicationClass = SolonMavenUtil.getStartClass(classesDirectory, mainClass, getLog());

            List<String> command = CommandLineBuilder.forMainClass(AOT_PROCESSOR_CLASS_NAME)
                    .withSystemProperties(this.systemPropertyVariables)
                    .withJvmArguments(new RunArguments(this.jvmArguments).asArray())
                    .withClasspath(getClassPath())
                    .withArguments(getAotArguments(applicationClass))
                    .build();

            getLog().info("Generating AOT assets using command: " + command);
            JavaProcessExecutor processExecutor = new JavaProcessExecutor(this.session, this.toolchainManager);
            processExecutor.run(this.project.getBasedir(), command, Collections.emptyMap());

        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private String[] getAotArguments(String applicationClass) {
        List<String> aotArguments = new ArrayList<>();
        aotArguments.add(applicationClass);
        aotArguments.add(classesDirectory.getAbsolutePath());
        aotArguments.add(this.project.getGroupId());
        aotArguments.add(this.project.getArtifactId());
        if (envs != null && envs.length != 0) {
            aotArguments.add("--solon.env=" + String.join(",", this.envs));
        }
        if (arguments != null) {
            Arrays.stream(arguments).filter(Objects::nonNull).forEach(aotArguments::add);
        }
        return aotArguments.toArray(new String[0]);
    }

    private URL[] getClassPath() {
        List<URL> urls = new ArrayList<>();
        urls.add(toURL(this.classesDirectory));

        Set<Artifact> artifacts = this.project.getArtifacts();
        List<URL> artifactUrlList = artifacts.stream()
                .filter(artifact -> !StringUtils.equals(artifact.getScope(), Artifact.SCOPE_TEST))
                .map(artifact -> toURL(artifact.getFile()))
                .collect(Collectors.toList());
        urls.addAll(artifactUrlList);
        return urls.toArray(new URL[0]);
    }

    protected URL toURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Invalid URL for " + file, ex);
        }
    }

}
