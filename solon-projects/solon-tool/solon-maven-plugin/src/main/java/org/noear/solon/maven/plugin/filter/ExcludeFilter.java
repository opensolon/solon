/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.maven.plugin.filter;

import org.apache.maven.artifact.Artifact;

import java.util.Arrays;
import java.util.List;

/**
 * An {DependencyFilter} that filters out any artifact matching an {@link Exclude}.
 *
 * @author Stephane Nicoll
 * @author David Turanski
 * @since 1.1.0
 */
public class ExcludeFilter extends DependencyFilter {

	public ExcludeFilter(Exclude... excludes) {
		this(Arrays.asList(excludes));
	}

	public ExcludeFilter(List<Exclude> excludes) {
		super(excludes);
	}

	@Override
	protected boolean filter(Artifact artifact) {
		for (FilterableDependency dependency : getFilters()) {
			if (equals(artifact, dependency)) {
				return true;
			}
		}
		return false;
	}

}
