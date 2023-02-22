package org.noear.solon.maven.plugin.tools.tool;

import java.io.File;

/**
 * Factory interface used to create a {@link Layout}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
public interface LayoutFactory {

	/**
	 * Return a {@link Layout} for the specified source file.
	 * @param source the source file
	 * @return the layout to use for the file
	 */
	Layout getLayout(File source);

}
