package org.noear.solon.maven.plugin.tools.jar;



import org.noear.solon.maven.plugin.tools.data.RandomAccessData;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;

/**
 * {@link Archive} implementation backed by a {@link JarFile}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class JarFileArchive implements Archive {

	private static final String UNPACK_MARKER = "UNPACK:";

	private static final int BUFFER_SIZE = 32 * 1024;

	private final JarFile jarFile;

	private URL url;

	private File tempUnpackFolder;

	public JarFileArchive(File file) throws IOException {
		this(file, null);
	}

	public JarFileArchive(File file, URL url) throws IOException {
		this(new JarFile(file));
		this.url = url;
	}

	public JarFileArchive(JarFile jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public URL getUrl() throws MalformedURLException {
		if (this.url != null) {
			return this.url;
		}
		return this.jarFile.getUrl();
	}

	@Override
	public Manifest getManifest() throws IOException {
		return this.jarFile.getManifest();
	}

	@Override
	public List<Archive> getNestedArchives(EntryFilter filter) throws IOException {
		List<Archive> nestedArchives = new ArrayList<Archive>();
		for (Entry entry : this) {
			if (filter.matches(entry)) {
				nestedArchives.add(getNestedArchive(entry));
			}
		}
		return Collections.unmodifiableList(nestedArchives);
	}

	@Override
	public Iterator<Entry> iterator() {
		return new EntryIterator(this.jarFile.entries());
	}

	protected Archive getNestedArchive(Entry entry) throws IOException {
		JarEntry jarEntry = ((JarFileEntry) entry).getJarEntry();
		if (jarEntry.getComment().startsWith(UNPACK_MARKER)) {
			return getUnpackedNestedArchive(jarEntry);
		}
		try {
			JarFile jarFile = this.jarFile.getNestedJarFile(jarEntry);
			return new JarFileArchive(jarFile);
		}
		catch (Exception ex) {
			throw new IllegalStateException(
					"Failed to get nested archive for entry " + entry.getName(), ex);
		}
	}

	private Archive getUnpackedNestedArchive(JarEntry jarEntry) throws IOException {
		String name = jarEntry.getName();
		if (name.lastIndexOf("/") != -1) {
			name = name.substring(name.lastIndexOf("/") + 1);
		}
		File file = new File(getTempUnpackFolder(), name);
		if (!file.exists() || file.length() != jarEntry.getSize()) {
			unpack(jarEntry, file);
		}
		return new JarFileArchive(file, file.toURI().toURL());
	}

	private File getTempUnpackFolder() {
		if (this.tempUnpackFolder == null) {
			File tempFolder = new File(System.getProperty("java.io.tmpdir"));
			this.tempUnpackFolder = createUnpackFolder(tempFolder);
		}
		return this.tempUnpackFolder;
	}

	private File createUnpackFolder(File parent) {
		int attempts = 0;
		while (attempts++ < 1000) {
			String fileName = new File(this.jarFile.getName()).getName();
			File unpackFolder = new File(parent,
					fileName + "-libs-" + UUID.randomUUID());
			if (unpackFolder.mkdirs()) {
				return unpackFolder;
			}
		}
		throw new IllegalStateException(
				"Failed to create unpack folder in directory '" + parent + "'");
	}

	private void unpack(JarEntry entry, File file) throws IOException {
		InputStream inputStream = this.jarFile.getInputStream(entry, RandomAccessData.ResourceAccess.ONCE);
		try {
			OutputStream outputStream = new FileOutputStream(file);
			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
			}
			finally {
				outputStream.close();
			}
		}
		finally {
			inputStream.close();
		}
	}

	@Override
	public String toString() {
		try {
			return getUrl().toString();
		}
		catch (Exception ex) {
			return "jar archive";
		}
	}

	/**
	 * {@link Entry} iterator implementation backed by {@link JarEntry}.
	 */
	private static class EntryIterator implements Iterator<Entry> {

		private final Enumeration<JarEntry> enumeration;

		EntryIterator(Enumeration<JarEntry> enumeration) {
			this.enumeration = enumeration;
		}

		@Override
		public boolean hasNext() {
			return this.enumeration.hasMoreElements();
		}

		@Override
		public Entry next() {
			return new JarFileEntry(this.enumeration.nextElement());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}

	}

	/**
	 * {@link Entry} implementation backed by a {@link JarEntry}.
	 */
	private static class JarFileEntry implements Entry {

		private final JarEntry jarEntry;

		JarFileEntry(JarEntry jarEntry) {
			this.jarEntry = jarEntry;
		}

		public JarEntry getJarEntry() {
			return this.jarEntry;
		}

		@Override
		public boolean isDirectory() {
			return this.jarEntry.isDirectory();
		}

		@Override
		public String getName() {
			return this.jarEntry.getName();
		}

	}

}
