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

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * Directory containing the generated sources.
     */
    @Parameter(defaultValue = "${project.build.directory}/solon-aot/main/sources", required = true)
    private File generatedSources;

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
        getLog().info("Aot process start ...");

        try {
            executeAot();
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    protected void executeAot() throws Exception {
        URL[] classPath = getClassPath();

        String applicationClass = SolonMavenUtil.getStartClass(classesDirectory, mainClass, getLog());

        List<String> command = CommandLineBuilder.forMainClass(AOT_PROCESSOR_CLASS_NAME)
                .withSystemProperties(this.systemPropertyVariables)
                .withJvmArguments(new RunArguments(this.jvmArguments).asArray())
                .withClasspath(classPath)
                .withArguments(getAotArguments(applicationClass))
                .build();

        getLog().info("Generating AOT assets using command: " + command);
        JavaProcessExecutor processExecutor = new JavaProcessExecutor(this.session, this.toolchainManager);
        processExecutor.run(this.project.getBasedir(), command, Collections.emptyMap());

        // 将 aot 阶段生成的 Java 文件编译成 class 文件
        compileSourceFiles(classPath);
        getLog().info("Aot process completed ...");
    }

    private void compileSourceFiles(URL[] classPath) throws Exception {
        List<Path> sourceFiles;
        if (!generatedSources.exists()) {
            return;
        }
        try (Stream<Path> pathStream = Files.walk(generatedSources.toPath())) {
            sourceFiles = pathStream.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        if (sourceFiles.isEmpty()) {
            return;
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            JavaCompilerPluginConfiguration compilerConfiguration = new JavaCompilerPluginConfiguration(this.project);
            List<String> options = new ArrayList<>();
            options.add("-cp");
            options.add(CommandLineBuilder.ClasspathBuilder.build(Arrays.asList(classPath)));
            options.add("-d");
            options.add(classesDirectory.toPath().toAbsolutePath().toString());
            String releaseVersion = compilerConfiguration.getReleaseVersion();
            if (releaseVersion != null) {
                options.add("--release");
                options.add(releaseVersion);
            }
            else {
                options.add("--source");
                options.add(compilerConfiguration.getSourceMajorVersion());
                options.add("--target");
                options.add(compilerConfiguration.getTargetMajorVersion());
            }
            options.addAll(new RunArguments(this.compilerArguments).getArgs());
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(asFiles(sourceFiles));
            Errors errors = new Errors();
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, errors, options, null, compilationUnits);
            boolean result = task.call();
            if (!result || errors.hasReportedErrors()) {
                throw new IllegalStateException("Unable to compile generated source" + errors);
            }
        }
    }

    private String[] getAotArguments(String applicationClass) {
        List<String> aotArguments = new ArrayList<>();
        aotArguments.add(applicationClass);
        aotArguments.add(classesDirectory.getAbsolutePath());
        aotArguments.add(generatedSources.getAbsolutePath());
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

    private Iterable<File> asFiles(final Iterable<? extends Path> paths) {
        return () -> new Iterator() {
            final Iterator<? extends Path> iter = paths.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public File next() {
                Path p = iter.next();
                try {
                    return p.toFile();
                } catch (UnsupportedOperationException e) {
                    throw new IllegalArgumentException(p.toString(), e);
                }
            }
        };
    }

    /**
     * {@link DiagnosticListener} used to collect errors.
     */
    protected static class Errors implements DiagnosticListener<JavaFileObject> {

        private final StringBuilder message = new StringBuilder();

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                this.message.append("\n");
                this.message.append(diagnostic.getMessage(Locale.getDefault()));
                if (diagnostic.getSource() != null) {
                    this.message.append(" ");
                    this.message.append(diagnostic.getSource().getName());
                    this.message.append(" ");
                    this.message.append(diagnostic.getLineNumber()).append(":").append(diagnostic.getColumnNumber());
                }
            }
        }

        boolean hasReportedErrors() {
            return this.message.length() > 0;
        }

        @Override
        public String toString() {
            return this.message.toString();
        }

    }

}
