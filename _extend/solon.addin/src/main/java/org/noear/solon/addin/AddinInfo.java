package org.noear.solon.addin;

import java.io.File;

/**
 * @author noear
 * @since 1.7
 */
public class AddinInfo {
    private final String name;
    private final File file;
    private AddinPackage addinPackage;

    public AddinInfo(String name, File file){
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public AddinPackage getAddinPackage() {
        return addinPackage;
    }

    protected void setAddinPackage(AddinPackage addinPackage) {
        this.addinPackage = addinPackage;
    }
}
