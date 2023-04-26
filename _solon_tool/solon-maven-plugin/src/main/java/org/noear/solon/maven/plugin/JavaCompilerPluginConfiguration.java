package org.noear.solon.maven.plugin;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.Arrays;

/**
 * Provides access to the Maven Java Compiler plugin configuration.
 *
 * @author Scott Frederick
 */
class JavaCompilerPluginConfiguration {

    private final MavenProject project;

    JavaCompilerPluginConfiguration(MavenProject project) {
        this.project = project;
    }

    String getSourceMajorVersion() {
        String version = getConfigurationValue("source");

        if (version == null) {
            version = getPropertyValue("maven.compiler.source");
        }

        return majorVersionFor(version);
    }

    String getTargetMajorVersion() {
        String version = getConfigurationValue("target");

        if (version == null) {
            version = getPropertyValue("maven.compiler.target");
        }

        return majorVersionFor(version);
    }

    String getReleaseVersion() {
        String version = getConfigurationValue("release");

        if (version == null) {
            version = getPropertyValue("maven.compiler.release");
        }

        return majorVersionFor(version);
    }

    private String getConfigurationValue(String propertyName) {
        Plugin plugin = this.project.getPlugin("org.apache.maven.plugins:maven-compiler-plugin");
        if (plugin != null) {
            Object pluginConfiguration = plugin.getConfiguration();
            if (pluginConfiguration instanceof Xpp3Dom) {
                return getNodeValue((Xpp3Dom) pluginConfiguration, propertyName);
            }
        }
        return null;
    }

    private String getPropertyValue(String propertyName) {
        if (this.project.getProperties().containsKey(propertyName)) {
            return this.project.getProperties().get(propertyName).toString();
        }
        return null;
    }

    private String getNodeValue(Xpp3Dom dom, String... childNames) {
        Xpp3Dom childNode = dom.getChild(childNames[0]);

        if (childNode == null) {
            return null;
        }

        if (childNames.length > 1) {
            return getNodeValue(childNode, Arrays.copyOfRange(childNames, 1, childNames.length));
        }

        return childNode.getValue();
    }

    private String majorVersionFor(String version) {
        if (version != null && version.startsWith("1.")) {
            return version.substring("1.".length());
        }
        return version;
    }

}
