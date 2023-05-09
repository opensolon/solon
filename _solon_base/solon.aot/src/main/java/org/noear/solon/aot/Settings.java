package org.noear.solon.aot;

import java.nio.file.Path;

/**
 * @author noear
 * @see 2.2
 */
public final class Settings {

    private Path classOutput;

    /**
     * aot生成的Java类
     */
    private Path generatedSources;

    private String groupId;

    private String artifactId;

    public Settings(Path classOutput, Path generatedSources, String groupId, String artifactId) {
        this.classOutput = classOutput;
        this.generatedSources = generatedSources;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public Path getClassOutput() {
        return classOutput;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public Path getGeneratedSources() {
        return generatedSources;
    }
}