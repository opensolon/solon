package com.layjava.docs.javadoc.solon.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * 多分组文档配置属性
 *
 * @author chengliang
 * @since  2024/04/16
 */
public class DocketProperty {

    private List<DocsProperty> docs;


    public List<DocsProperty> getDocs() {
        if (docs == null) {
            docs =  new ArrayList<>(3);
        }
        return docs;
    }

    public void setDocs(List<DocsProperty> docs) {
        this.docs = docs;
    }

    @Override
    public String toString() {
        return "DocketProperty{" +
                "docs=" + docs +
                '}';
    }

}
