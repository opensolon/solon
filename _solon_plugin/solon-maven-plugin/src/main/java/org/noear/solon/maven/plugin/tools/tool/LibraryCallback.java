package org.noear.solon.maven.plugin.tools.tool;

import java.io.File;
import java.io.IOException;

/**
 * Callback interface used to iterate {@link Libraries}.
 *
 * @author Phillip Webb
 */
public interface LibraryCallback {

	/**
	 * Callback for a single library backed by a {@link File}.
	 * @param library the library
	 * @throws IOException if the operation fails
	 */
	void library(Library library) throws IOException;

}
