package org.noear.solon.maven.plugin.tools.jar;

/**
 * Interface that can be used to filter and optionally rename jar entries.
 *
 * @author Phillip Webb
 */
interface JarEntryFilter {

	/**
	 * Apply the jar entry filter.
	 * @param name the current entry name. This may be different that the original entry
	 * name if a previous filter has been applied
	 * @return the new name of the entry or {@code null} if the entry should not be
	 * included.
	 */
	AsciiBytes apply(AsciiBytes name);

}
