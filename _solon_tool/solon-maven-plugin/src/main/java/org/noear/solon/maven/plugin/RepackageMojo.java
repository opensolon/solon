package org.noear.solon.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import org.apache.maven.shared.artifact.filter.collection.ScopeFilter;
import org.noear.solon.maven.plugin.filter.Exclude;
import org.noear.solon.maven.plugin.filter.ExcludeFilter;
import org.noear.solon.maven.plugin.filter.FilterableDependency;
import org.noear.solon.maven.plugin.filter.Include;
import org.noear.solon.maven.plugin.filter.IncludeFilter;
import org.noear.solon.maven.plugin.filter.MatchingGroupIdFilter;
import org.noear.solon.maven.plugin.tools.tool.ArtifactsLibraries;
import org.noear.solon.maven.plugin.tools.tool.Libraries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

//@Mojo(name = "repackage", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
@Mojo(name = "repackage", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class RepackageMojo extends AbstractMojo {

    @Parameter(required = false)
    private String mainClass;

    @Parameter(readonly = false)
    private String jvmArguments;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", required = true)
    private String finalName;

    @Parameter(defaultValue = "${project.packaging}", required = true)
    private String packaging;

    @Parameter
    private String classifier;

    /**
     * Include system scoped dependencies.
     */
    @Parameter(defaultValue = "true")
    public boolean includeSystemScope;

    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;

    /**
     * Collection of artifact definitions to include. The {@link FilterableDependency} element defines
     * mandatory {@code groupId} and {@code artifactId} properties and an optional
     * mandatory {@code groupId} and {@code artifactId} properties and an optional
     * {@code classifier} property.
     * @since 1.2.0
     */
    @Parameter(property = "solon.includes")
    private List<Include> includes;

    /**
     * Collection of artifact definitions to exclude. The {@link FilterableDependency} element defines
     * mandatory {@code groupId} and {@code artifactId} properties and an optional
     * {@code classifier} property.
     * @since 1.1.0
     */
    @Parameter(property = "solon.excludes")
    private List<Exclude> excludes;

    /**
     * Comma separated list of groupId names to exclude (exact match).
     * @since 1.1.0
     */
    @Parameter(property = "solon.excludeGroupIds", defaultValue = "")
    private String excludeGroupIds;

    public static PluginType PLUGIN_TYPE;

    private final Log logger = getLog();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logger.info("打包类型：" + packaging);
        if (packaging != null) {
            if ("jar".equalsIgnoreCase(packaging)) {
                PLUGIN_TYPE = PluginType.JAR;
                //移动数据
                ClassesMove.change(project.getArtifact().getFile());
                //处理依赖
                repackage();
                //处理loader
                try {
                    CopyLoader.start(getTargetFile());
                } catch (Exception e) {
                    throw new MojoExecutionException("write loader exception", e);
                }
            } else if ("war".equalsIgnoreCase(packaging)) {
                PLUGIN_TYPE = PluginType.WAR;
                //默认就是war了
            }
        } else {
            throw new MojoExecutionException("打包方式不是JAR或者WAR类型");
        }

    }

    private void repackage() throws MojoExecutionException, MojoFailureException {
        try {
            File sourceFile = project.getArtifact().getFile();
            Repackager repackager = new Repackager(sourceFile, logger, mainClass);
            File target = getTargetFile();
            Set<Artifact> artifacts = project.getArtifacts();

            Set<Artifact> includedArtifacts = filterDependencies(artifacts, getAdditionalFilters());
            Libraries libraries = new ArtifactsLibraries(includedArtifacts, Collections.emptyList(), getLog());
            repackager.repackage(target, libraries);
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private ArtifactsFilter[] getAdditionalFilters() {
        List<ArtifactsFilter> filters = new ArrayList<>();
        if (!this.includeSystemScope) {
            filters.add(new ScopeFilter(null, Artifact.SCOPE_SYSTEM));
        }
        filters.add(new ScopeFilter(null, Artifact.SCOPE_PROVIDED));
        return filters.toArray(new ArtifactsFilter[0]);
    }

    protected final Set<Artifact> filterDependencies(Set<Artifact> dependencies, ArtifactsFilter... additionalFilters)
            throws MojoExecutionException {
        try {
            Set<Artifact> filtered = new LinkedHashSet<>(dependencies);
            filtered.retainAll(getFilters(additionalFilters).filter(dependencies));
            return filtered;
        }
        catch (ArtifactFilterException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    /**
     * Return artifact filters configured for this MOJO.
     * @param additionalFilters optional additional filters to apply
     * @return the filters
     */
    private FilterArtifacts getFilters(ArtifactsFilter... additionalFilters) {
        FilterArtifacts filters = new FilterArtifacts();
        for (ArtifactsFilter additionalFilter : additionalFilters) {
            filters.addFilter(additionalFilter);
        }
        filters.addFilter(new MatchingGroupIdFilter(cleanFilterConfig(this.excludeGroupIds)));
        if (this.includes != null && !this.includes.isEmpty()) {
            filters.addFilter(new IncludeFilter(this.includes));
        }
        if (this.excludes != null && !this.excludes.isEmpty()) {
            filters.addFilter(new ExcludeFilter(this.excludes));
        }
        return filters;
    }

    private String cleanFilterConfig(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        StringBuilder cleaned = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(content, ",");
        while (tokenizer.hasMoreElements()) {
            cleaned.append(tokenizer.nextToken().trim());
            if (tokenizer.hasMoreElements()) {
                cleaned.append(",");
            }
        }
        return cleaned.toString();
    }

    private File getTargetFile() {
        String classifier = (this.classifier != null ? this.classifier.trim() : "");
        if (classifier.length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }
        if (!this.outputDirectory.exists()) {
            this.outputDirectory.mkdirs();
        }
        String name = this.finalName + classifier + "." + this.project.getArtifact().getArtifactHandler().getExtension();
        return new File(this.outputDirectory, name);
    }
}