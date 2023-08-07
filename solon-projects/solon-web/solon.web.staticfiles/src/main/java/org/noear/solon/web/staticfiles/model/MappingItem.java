package org.noear.solon.web.staticfiles.model;

import java.io.Serializable;

/**
 * @author noear
 * @since 2.1
 */
public class MappingItem implements Serializable {
    private String path;
    private String repository;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }
}
