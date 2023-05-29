package org.noear.solon.maven.plugin.tools.tool;

/**
 * A specialization of {@link Layout} that repackages an existing archive by moving its
 * content to a new location.
 *
 * @author Andy Wilkinson
 * @since 1.4.0
 */
public interface RepackagingLayout extends Layout {

	/**
	 * Returns the location to which classes should be moved.
	 * @return the repackaged classes location
	 */
	String getRepackagedClassesLocation();

}
