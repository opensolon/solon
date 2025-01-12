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
package org.noear.solon.maven.plugin.tools.tool;


import org.noear.solon.maven.plugin.Constant;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Common {@link Layout}s.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 */
public final class Layouts {

	private Layouts() {
	}

	/**
	 * Return a layout for the given source file.
	 * @param file the source file
	 * @return a {@link Layout}
	 */
	public static Layout forFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File must not be null");
		}
		if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
			return new Jar();
		}
		if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
			return new War();
		}
		throw new IllegalStateException("Unable to deduce layout for '" + file + "'");
	}


	public static class War implements RepackagingLayout {

		@Override
		public String getLibraryDestination(String libraryName, LibraryScope scope) {
			return Constant.WAR_LIB_PATH;
		}

		@Override
		public String getRepackagedClassesLocation() {
			return "";
		}
	}

	/**
	 * Executable JAR layout.
	 */
	public static class Jar implements RepackagingLayout {

		@Override
		public String getLibraryDestination(String libraryName, LibraryScope scope) {
			return Constant.LIB_PATH;
		}

		@Override
		public String getRepackagedClassesLocation() {
			return "";
		}
	}

	/**
	 * Module layout (designed to be used as a "plug-in").
	 *
	 * @deprecated as of 1.5 in favor of a custom {@link LayoutFactory}
	 */
	@Deprecated
	public static class Module implements Layout {

		private static final Set<LibraryScope> LIB_DESTINATION_SCOPES = new HashSet<LibraryScope>(
				Arrays.asList(LibraryScope.COMPILE, LibraryScope.RUNTIME,
						LibraryScope.CUSTOM));

		@Override
		public String getLibraryDestination(String libraryName, LibraryScope scope) {
			if (LIB_DESTINATION_SCOPES.contains(scope)) {
				return Constant.LIB_PATH;
			}
			return null;
		}
	}
}
