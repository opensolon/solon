package org.noear.solon.maven.plugin.tools.jar;

import java.util.zip.ZipEntry;

/**
 * A file header record that has been loaded from a Jar file.
 *
 * @author Phillip Webb
 * @see JarEntry
 * @see CentralDirectoryFileHeader
 */
interface FileHeader {

	/**
	 * Returns {@code true} if the header has the given name.
	 * @param name the name to test
	 * @param suffix an additional suffix (or {@code null})
	 * @return {@code true} if the header has the given name
	 */
	boolean hasName(String name, String suffix);

	/**
	 * Return the offset of the load file header within the archive data.
	 * @return the local header offset
	 */
	long getLocalHeaderOffset();

	/**
	 * Return the compressed size of the entry.
	 * @return the compressed size.
	 */
	long getCompressedSize();

	/**
	 * Return the uncompressed size of the entry.
	 * @return the uncompressed size.
	 */
	long getSize();

	/**
	 * Return the method used to compress the data.
	 * @return the zip compression method
	 * @see ZipEntry#STORED
	 * @see ZipEntry#DEFLATED
	 */
	int getMethod();

}
