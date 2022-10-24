package org.noear.solon.maven.plugin;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.maven.plugin.logging.Log;
import org.noear.solon.maven.plugin.tools.tool.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static org.noear.solon.maven.plugin.Constant.*;


/**
 * @author hxm
 */
public class Repackager {

    private static final byte[] ZIP_FILE_HEADER = new byte[]{'P', 'K', 3, 4};

    private final File source;
    private final String mainClass;

    private Layout layout;

    private final Log logger;

    public Repackager(File source, Log logger, String mainClass) throws Exception {
        this.logger = logger;
        if (source == null) {
            throw new IllegalArgumentException("Source file must be provided");
        }
        if (!source.exists() || !source.isFile()) {
            throw new IllegalArgumentException("Source must refer to an existing file, "
                    + "got " + source.getAbsolutePath());
        }
        this.source = source.getAbsoluteFile();

        if (mainClass == null || mainClass.trim().length() == 0) {
            this.mainClass = getStartClass();
        } else {
            this.mainClass = mainClass;
        }
        logger.info("JAR的启动类为：" + this.mainClass);
    }


    private String getStartClass() throws IOException {
        File f = getFile();
        JarFile jar = new JarFile(f);
        try {
            Enumeration<JarEntry> enumFiles = jar.entries();
            List<String> mains = new ArrayList<>();
            ClassPool classPool= ClassPool.getDefault();
            while (enumFiles.hasMoreElements()) {
                JarEntry entry = enumFiles.nextElement();
                String classFullName = entry.getName();
                if (classFullName.endsWith(".class")) {
                    try {
                        InputStream inputStream = jar.getInputStream(entry);
                        CtClass ctClass = classPool.makeClass(inputStream);
                        inputStream.close();
                        CtMethod[] methods = ctClass.getDeclaredMethods();
                        for (CtMethod method : methods) {
                            if (method.getName().equals("main") && method.getParameterTypes().length == 1) {
                                if (method.getParameterTypes()[0].getName().equals("java.lang.String[]") && Modifier.isStatic(method.getModifiers())) {
                                    if (method.getReturnType().getName().equals("void")) {
                                        logger.info("检查到的启动类：" + ctClass.getName());
                                        mains.add(ctClass.getName());
                                    }
                                }
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }

            if (mains.size() > 0) {
                if (mains.size() == 1) {
                    return mains.get(0);
                }
                for (int i = 0; i < mains.size(); i++) {
                    logger.info(i + "、" + mains.get(i));
                }
                logger.info("请选择一个主函数作为启动函数(如：输入0回车)：");
                String tempMain;
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    if (scanner.hasNextLine()) {
                        try {
                            String s = scanner.nextLine();
                            int i = Integer.parseInt(s);
                            tempMain = mains.get(i);
                            break;
                        } catch (Exception e) {
                            logger.error("选择失败，请重新选择");
                        }
                    }
                }
                scanner.close();
                return tempMain;
            }
        }finally {
            jar.close();
        }
        throw new IllegalStateException("找不到启动类,请配置你的启动类");
    }


    /**
     * Repackage to the given destination so that it can be launched using '
     * {@literal java -jar}'.
     *
     * @param destination the destination file (may be the same as the source)
     * @param libraries   the libraries required to run the archive
     * @throws IOException if the file cannot be repackaged
     */
    public void repackage(File destination, Libraries libraries) throws IOException {
        if (destination == null || destination.isDirectory()) {
            throw new IllegalArgumentException("Invalid destination");
        }
        if (libraries == null) {
            throw new IllegalArgumentException("Libraries must not be null");
        }
        if (this.layout == null) {
            this.layout = getLayoutFactory().getLayout(this.source);
        }
        if (alreadyRepackaged()) {
            return;
        }
        destination = destination.getAbsoluteFile();
        File workingSource = this.source;
        if (this.source.equals(destination)) {
            workingSource = getBackupFile();
            workingSource.delete();
            renameFile(this.source, workingSource);
        }
        destination.delete();
        JarFile jarFileSource = new JarFile(workingSource);
        try {
            repackage(jarFileSource, destination, libraries);
        } finally {
            jarFileSource.close();
        }
    }

    private LayoutFactory getLayoutFactory() {
        return new DefaultLayoutFactory();
    }

    /**
     * Return the {@link File} to use to backup the original source.
     *
     * @return the file to use to backup the original source
     */
    public final File getBackupFile() {
        return new File(this.source.getParentFile(), this.source.getName() + ".original");
    }

    public final File getFile() {
        return new File(this.source.getParentFile(), this.source.getName());
    }

    private boolean alreadyRepackaged() throws IOException {
        JarFile jarFile = new JarFile(this.source);
        try {
            Manifest manifest = jarFile.getManifest();
            return (manifest != null && manifest.getMainAttributes()
                    .getValue(JAR_TOOL_VALUE) != null);
        } finally {
            jarFile.close();
        }
    }

    private void repackage(JarFile sourceJar, File destination, Libraries libraries) throws IOException {
        JarWriter writer = new JarWriter(destination);
        try {
            final List<Library> unpackLibraries = new ArrayList<>();
            final List<Library> standardLibraries = new ArrayList<>();
            libraries.doWithLibraries(library -> {
                File file = library.getFile();
                if (isZip(file)) {
                    if (library.isUnpackRequired()) {
                        unpackLibraries.add(library);
                    } else {
                        standardLibraries.add(library);
                    }
                }
            });
            repackage(sourceJar, writer, unpackLibraries, standardLibraries);
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    private void repackage(JarFile sourceJar, JarWriter writer,
                           final List<Library> unpackLibraries, final List<Library> standardLibraries)
            throws IOException {
        writer.writeManifest(buildManifest(sourceJar));
        Set<String> seen = new HashSet<>();
        writeNestedLibraries(unpackLibraries, seen, writer);
        if (this.layout instanceof RepackagingLayout) {
            writer.writeEntries(sourceJar, new RenamingEntryTransformer(
                    ((RepackagingLayout) this.layout).getRepackagedClassesLocation()));
        } else {
            writer.writeEntries(sourceJar);
        }
        writeNestedLibraries(standardLibraries, seen, writer);
    }

    private void writeNestedLibraries(List<Library> libraries, Set<String> alreadySeen,
                                      JarWriter writer) throws IOException {
        for (Library library : libraries) {
            String destination = Repackager.this.layout
                    .getLibraryDestination(library.getName(), library.getScope());
            if (destination != null) {
                if (!alreadySeen.add(destination + library.getName())) {
                    throw new IllegalStateException(
                            "Duplicate library " + library.getName());
                }
                writer.writeNestedLibrary(destination, library);
            }
        }
    }

    private boolean isZip(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                return isZip(fileInputStream);
            } finally {
                fileInputStream.close();
            }
        } catch (IOException ex) {
            return false;
        }
    }

    private boolean isZip(InputStream inputStream) throws IOException {
        for (int i = 0; i < ZIP_FILE_HEADER.length; i++) {
            if (inputStream.read() != ZIP_FILE_HEADER[i]) {
                return false;
            }
        }
        return true;
    }

    private Manifest buildManifest(JarFile source) throws IOException {
        Manifest manifest = source.getManifest();
        if (manifest == null) {
            manifest = new Manifest();
        }
        manifest = new Manifest(manifest);
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue(JAR_TOOL, JAR_TOOL_VALUE);
        manifest.getMainAttributes().putValue(START_CLASS, mainClass);
        manifest.getMainAttributes().putValue("Main-Class", MAIN_CLASS);
        return manifest;
    }


    private void renameFile(File file, File dest) {
        if (!file.renameTo(dest)) {
            throw new IllegalStateException(
                    "Unable to rename '" + file + "' to '" + dest + "'");
        }
    }

    /**
     * An {@code EntryTransformer} that renames entries by applying a prefix.
     */
    private static final class RenamingEntryTransformer implements JarWriter.EntryTransformer {

        private final String namePrefix;

        private RenamingEntryTransformer(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public JarEntry transform(JarEntry entry) {
            if (entry.getName().equals("META-INF/INDEX.LIST")) {
                return null;
            }
            if ((entry.getName().startsWith("META-INF/")
                    && !entry.getName().equals("META-INF/aop.xml"))
                    || entry.getName().startsWith("BOOT-INF/")) {
                return entry;
            }
            JarEntry renamedEntry = new JarEntry(this.namePrefix + entry.getName());
            renamedEntry.setTime(entry.getTime());
            renamedEntry.setSize(entry.getSize());
            renamedEntry.setMethod(entry.getMethod());
            if (entry.getComment() != null) {
                renamedEntry.setComment(entry.getComment());
            }
            renamedEntry.setCompressedSize(entry.getCompressedSize());
            renamedEntry.setCrc(entry.getCrc());
            setCreationTimeIfPossible(entry, renamedEntry);
            if (entry.getExtra() != null) {
                renamedEntry.setExtra(entry.getExtra());
            }
            setLastAccessTimeIfPossible(entry, renamedEntry);
            setLastModifiedTimeIfPossible(entry, renamedEntry);
            return renamedEntry;
        }

        private void setCreationTimeIfPossible(JarEntry source, JarEntry target) {
            try {
                if (source.getCreationTime() != null) {
                    target.setCreationTime(source.getCreationTime());
                }
            } catch (NoSuchMethodError ex) {
                // Not running on Java 8. Continue.
            }
        }

        private void setLastAccessTimeIfPossible(JarEntry source, JarEntry target) {
            try {
                if (source.getLastAccessTime() != null) {
                    target.setLastAccessTime(source.getLastAccessTime());
                }
            } catch (NoSuchMethodError ex) {
                // Not running on Java 8. Continue.
            }
        }

        private void setLastModifiedTimeIfPossible(JarEntry source, JarEntry target) {
            try {
                if (source.getLastModifiedTime() != null) {
                    target.setLastModifiedTime(source.getLastModifiedTime());
                }
            } catch (NoSuchMethodError ex) {
                // Not running on Java 8. Continue.
            }
        }


    }

}
