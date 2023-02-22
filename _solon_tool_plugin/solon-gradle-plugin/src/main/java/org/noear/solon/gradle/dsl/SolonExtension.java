package org.noear.solon.gradle.dsl;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

public class SolonExtension {

    private final Property<String> mainClass;

    public SolonExtension(Project project) {
        this.mainClass = project.getObjects().property(String.class);
    }

    public Property<String> getMainClass() {
        return this.mainClass;
    }
}
