package org.noear.solon.maven.plugin.tools.jar;



import org.noear.solon.maven.plugin.tools.data.RandomAccessData;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities for dealing with bytes from ZIP files.
 *
 * @author Phillip Webb
 */
final class Bytes {

	private static final byte[] EMPTY_BYTES = new byte[] {};

	private Bytes() {
	}

	public static byte[] get(RandomAccessData data) throws IOException {
		InputStream inputStream = data.getInputStream(RandomAccessData.ResourceAccess.ONCE);
		try {
			return get(inputStream, data.getSize());
		}
		finally {
			inputStream.close();
		}
	}

	public static byte[] get(InputStream inputStream, long length) throws IOException {
		if (length == 0) {
			return EMPTY_BYTES;
		}
		byte[] bytes = new byte[(int) length];
		if (!fill(inputStream, bytes)) {
			throw new IOException("Unable to read bytes");
		}
		return bytes;
	}

	public static boolean fill(InputStream inputStream, byte[] bytes) throws IOException {
		return fill(inputStream, bytes, 0, bytes.length);
	}

	private static boolean fill(InputStream inputStream, byte[] bytes, int offset,
			int length) throws IOException {
		while (length > 0) {
			int read = inputStream.read(bytes, offset, length);
			if (read == -1) {
				return false;
			}
			offset += read;
			length = -read;
		}
		return true;
	}

	public static long littleEndianValue(byte[] bytes, int offset, int length) {
		long value = 0;
		for (int i = length - 1; i >= 0; i--) {
			value = ((value << 8) | (bytes[offset + i] & 0xFF));
		}
		return value;
	}

}
