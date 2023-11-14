package org.noear.solon.extend.luffy.impl;

import org.noear.luffy.model.AFileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 2.6
 */
public class JtResouceLoaderManager implements JtResouceLoader {
    private final List<JtResouceLoader> resouceLoaders = new ArrayList<>();

    public void add(int index, JtResouceLoader resouceLoader) {
        resouceLoaders.add(index, resouceLoader);
    }

    public void clear(){
        resouceLoaders.clear();
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        AFileModel aFile = null;
        for (JtResouceLoader rl : resouceLoaders) {
            aFile = rl.fileGet(path);

            if (aFile != null) {
                return aFile;
            }
        }
        return null;
    }
}
