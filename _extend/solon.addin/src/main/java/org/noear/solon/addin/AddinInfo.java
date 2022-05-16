package org.noear.solon.addin;

import java.io.File;

/**
 * 外接小程序信息
 *
 * @author noear
 * @since 1.7
 */
public class AddinInfo {
    /**
     * 插件名
     */
    private final String name;
    /**
     * 对应文件
     */
    private final File file;
    /**
     * 插件包
     */
    private AddinPackage addinPackage;

    public AddinInfo(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public boolean getStarted() {
        if (addinPackage == null) {
            return false;
        } else {
            return addinPackage.getStarted();
        }
    }


    public AddinPackage getAddinPackage() {
        return addinPackage;
    }

    ///////

    protected void setAddinPackage(AddinPackage addinPackage) {
        this.addinPackage = addinPackage;
    }
}
