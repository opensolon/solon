package org.noear.solon.maven.plugin.tools.tool;

import java.io.IOException;

/**
 * Encapsulates information about libraries that may be packed into the archive.
 *
 * @author Phillip Webb
 */
public interface Libraries {

	/**
	 * Represents no libraries.
	 */
	Libraries NONE = new Libraries() {
		@Override
		public void doWithLibraries(LibraryCallback callback) throws IOException {
		}
	};

	/**
	 * Iterate all relevant libraries.
	 * @param callback a callback for each relevant library.
	 * @throws IOException if the operation fails
	 */
	void doWithLibraries(LibraryCallback callback) throws IOException;

}
