package org.noear.solon.maven.plugin.tools.tool;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.*;

/**
 * {@link Libraries} backed by Maven {@link Artifact}s.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class ArtifactsLibraries implements Libraries {

	private static final Map<String, LibraryScope> SCOPES;

	static {
		Map<String, LibraryScope> libraryScopes = new HashMap<String, LibraryScope>();
		libraryScopes.put(Artifact.SCOPE_COMPILE, LibraryScope.COMPILE);
		libraryScopes.put(Artifact.SCOPE_RUNTIME, LibraryScope.RUNTIME);
		libraryScopes.put(Artifact.SCOPE_PROVIDED, LibraryScope.PROVIDED);
		libraryScopes.put(Artifact.SCOPE_SYSTEM, LibraryScope.PROVIDED);
		SCOPES = Collections.unmodifiableMap(libraryScopes);
	}

	private final Set<Artifact> artifacts;

	private final Collection<Dependency> unpacks;

	private final Log log;

	public ArtifactsLibraries(Set<Artifact> artifacts, Collection<Dependency> unpacks,
			Log log) {
		this.artifacts = artifacts;
		this.unpacks = unpacks;
		this.log = log;
	}

	@Override
	public void doWithLibraries(LibraryCallback callback) throws IOException {
		Set<String> duplicates = getDuplicates(this.artifacts);
		for (Artifact artifact : this.artifacts) {
			LibraryScope scope = SCOPES.get(artifact.getScope());
			if (scope != null && artifact.getFile() != null) {
				String name = getFileName(artifact);
				if (duplicates.contains(name)) {
					this.log.debug("Duplicate found: " + name);
					name = artifact.getGroupId() + "-" + name;
					this.log.debug("Renamed to: " + name);
				}
				callback.library(new Library(name, artifact.getFile(), scope,
						isUnpackRequired(artifact)));
			}
		}
	}

	private Set<String> getDuplicates(Set<Artifact> artifacts) {
		Set<String> duplicates = new HashSet<String>();
		Set<String> seen = new HashSet<String>();
		for (Artifact artifact : artifacts) {
			String fileName = getFileName(artifact);
			if (artifact.getFile() != null && !seen.add(fileName)) {
				duplicates.add(fileName);
			}
		}
		return duplicates;
	}

	private boolean isUnpackRequired(Artifact artifact) {
		if (this.unpacks != null) {
			for (Dependency unpack : this.unpacks) {
				if (artifact.getGroupId().equals(unpack.getGroupId())
						&& artifact.getArtifactId().equals(unpack.getArtifactId())) {
					return true;
				}
			}
		}
		return false;
	}

	private String getFileName(Artifact artifact) {
		StringBuilder sb = new StringBuilder();
		sb.append(artifact.getArtifactId()).append("-").append(artifact.getBaseVersion());
		String classifier = artifact.getClassifier();
		if (classifier != null) {
			sb.append("-").append(classifier);
		}
		sb.append(".").append(artifact.getArtifactHandler().getExtension());
		return sb.toString();
	}

}
