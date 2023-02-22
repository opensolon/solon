package org.noear.solon.maven.plugin.tools.jar;


import org.noear.solon.maven.plugin.tools.data.RandomAccessData;

/**
 * Callback visitor triggered by {@link CentralDirectoryParser}.
 *
 * @author Phillip Webb
 */
interface CentralDirectoryVisitor {

	void visitStart(CentralDirectoryEndRecord endRecord,
                    RandomAccessData centralDirectoryData);

	void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset);

	void visitEnd();

}
