package org.noear.solon.gradle.tasks.bundling;

import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.bundling.War;

import java.util.Collections;

public abstract class SolonWar extends War implements SolonArchive {

    private final SolonArchiveSupport support;

    private final Provider<String> projectName;

    private final Provider<Object> projectVersion;

    private FileCollection providedClasspath;

    public SolonWar() {
        this.support = new SolonArchiveSupport();
        setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);

        Project project = getProject();
        this.projectName = project.provider(project::getName);
        this.projectVersion = project.provider(project::getVersion);
    }

    @Override
    public void copy() {
        this.support.configureManifest(
                getManifest(),
                getMainClass().get(),
                this.getTargetJavaVersion().get().toString(),
                this.projectName.get(),
                this.projectVersion.get()
        );
        
        super.copy();
    }

    /**
     * Adds files to the provided classpath to include in the {@code WEB-INF/lib-provided}
     * directory of the war. The given {@code classpath} is evaluated as per
     * {@link Project#files(Object...)}.
     *
     * @param classpath the additions to the classpath
     */
    public void providedClasspath(Object... classpath) {
        FileCollection existingClasspath = this.providedClasspath;
        this.providedClasspath = getProject()
                .files((existingClasspath != null) ? existingClasspath : Collections.emptyList(), classpath);
    }

}
