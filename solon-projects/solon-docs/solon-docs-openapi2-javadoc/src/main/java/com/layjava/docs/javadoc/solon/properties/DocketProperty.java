package com.layjava.docs.javadoc.solon.properties;


import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;

@Configuration
@Inject("${solon}")
public class DocketProperty {

    private List<DocsProperty> docs;


    public List<DocsProperty> getDocs() {
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
