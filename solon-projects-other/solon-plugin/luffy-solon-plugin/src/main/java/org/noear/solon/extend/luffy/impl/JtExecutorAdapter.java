package org.noear.solon.extend.luffy.impl;

import org.noear.luffy.executor.IJtConfigAdapter;
import org.noear.luffy.executor.IJtExecutorAdapter;
import org.noear.luffy.model.AFileModel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.logging.utils.TagsMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 执行工厂适配器
 *
 * @author noear
 * @since 1.3
 * */
public class JtExecutorAdapter implements IJtExecutorAdapter, IJtConfigAdapter {

    static Logger log = LoggerFactory.getLogger(JtExecutorAdapter.class);

    private String _defaultExecutor;
    private String _defLogTag;

    private JtResouceLoader forDebug;
    private JtResouceLoader forRelease;


    public JtExecutorAdapter(JtResouceLoader resouceLoader) {
        _defaultExecutor = JtMapping.getActuator("");
        _defLogTag = "luffy";

        forDebug = new JtResouceLoaderDebug();
        forRelease = resouceLoader;
    }

    @Override
    public void log(AFileModel file, Map<String, Object> data) {
        if (file == null) {
            Context ctx = Context.current();

            if (ctx != null) {
                file = ctx.attr("file");
            }
        }

        if (data.containsKey("tag") == false) {
            TagsMDC.tag0(_defLogTag);
        }

        if (file != null) {
            if (data.containsKey("tag2") == false) {
                TagsMDC.tag2(file.path);
            }
        }

        log.debug("{}", data);
    }

    @Override
    public void logError(AFileModel file, String msg, Throwable err) {
        TagsMDC.tag0(_defLogTag);
        TagsMDC.tag2(file.path);

        if (err == null) {
            log.error("{}", msg);
        } else {
            log.error("{}\r\n{}", msg, err);
        }
    }


    @Override
    public AFileModel fileGet(String path) throws Exception {
        AFileModel file = null;

        if (Solon.cfg().isDebugMode()) {
            file = forDebug.fileGet(path);
        }

        if (file == null) {
            file = forRelease.fileGet(path);
        }

        return file;
    }

    private String _nodeId;

    @Override
    public String nodeId() {
        if (_nodeId == null) {
            _nodeId = Utils.guid();
        }

        return _nodeId;
    }

    @Override
    public String defaultExecutor() {
        return _defaultExecutor;
    }

    public void defaultExecutorSet(String defaultExecutor) {
        _defaultExecutor = defaultExecutor;
    }

    @Override
    public String cfgGet(String name, String def) throws Exception {
        if (Utils.isEmpty(name)) {
            return def;
        }

        return Solon.cfg().get(name, def);
    }

    @Override
    public boolean cfgSet(String name, String value) throws Exception {
        if (Utils.isEmpty(name)) {
            return false;
        } else {
            Solon.cfg().setProperty(name, value);

            return true;
        }
    }
}
