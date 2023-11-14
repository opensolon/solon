package org.noear.solon.extend.luffy.impl;

import org.noear.luffy.model.AFileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear
 * @since 2.6
 */
public class JtResouceLoaderComposite implements JtResouceLoader{
    private List<JtResouceLoader> resouceLoaders = new ArrayList<>();

    public void add(JtResouceLoader resouceLoader){
        resouceLoaders.add(resouceLoader);
    }

    @Override
    public AFileModel fileGet(String path) throws Exception {
        return null;
    }
}
