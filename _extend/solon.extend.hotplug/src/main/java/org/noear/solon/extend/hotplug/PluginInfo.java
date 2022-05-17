package org.noear.solon.extend.hotplug;

import java.io.File;

/**
 * 外接小程序信息
 *
 * @author noear
 * @since 1.7
 */
public class PluginInfo {
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
    private PluginPackage addinPackage;

    public PluginInfo(String name, File file) {
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


    public PluginPackage getAddinPackage() {
        return addinPackage;
    }

    ///////

    protected void setAddinPackage(PluginPackage addinPackage) {
        this.addinPackage = addinPackage;
    }
}
