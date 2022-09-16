package org.noear.solon.maven.plugin.tools.data;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that provides read-only random access to some underlying data.
 * Implementations must allow concurrent reads in a thread-safe manner.
 *
 * @author Phillip Webb
 */
public interface RandomAccessData {

	/**
	 * Returns an {@link InputStream} that can be used to read the underlying data. The
	 * caller is responsible close the underlying stream.
	 * @param access hint indicating how the underlying data should be accessed
	 * @return a new input stream that can be used to read the underlying data.
	 * @throws IOException if the stream cannot be opened
	 */
	InputStream getInputStream(ResourceAccess access) throws IOException;

	/**
	 * Returns a new {@link RandomAccessData} for a specific subsection of this data.
	 * @param offset the offset of the subsection
	 * @param length the length of the subsection
	 * @return the subsection data
	 */
	RandomAccessData getSubsection(long offset, long length);

	/**
	 * Returns the size of the data.
	 * @return the size
	 */
	long getSize();

	/**
	 * Lock modes for accessing the underlying resource.
	 */
	enum ResourceAccess {

		/**
		 * Obtain access to the underlying resource once and keep it until the stream is
		 * closed.
		 */
		ONCE,

		/**
		 * Obtain access to the underlying resource on each read, releasing it when done.
		 */
		PER_READ

	}

}
