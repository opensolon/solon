package org.noear.solon.ai.rag.loader;

/**
 * PDF 加载模式
 *
 * @author 小奶奶花生米
 * @date 2025-02-21
 */
public enum PdfLoadMode {

    /**
     * 整个文档作为一个 Document
     */
    SINGLE,
    /**
     * 每页作为一个 Document
     */
    PAGE
}
