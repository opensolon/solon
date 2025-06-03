/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.luffy.impl;

import org.noear.luffy.executor.IJtConfigAdapter;
import org.noear.luffy.executor.IJtExecutorAdapter;
import org.noear.luffy.model.AFileModel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.logging.utils.TagsMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行工厂适配器
 *
 * @author noear
 * @since 1.3
 */
public class JtExecutorAdapter implements IJtExecutorAdapter, IJtConfigAdapter {

    static Logger log = LoggerFactory.getLogger(JtExecutorAdapter.class);

    private String _defaultExecutor;
    private String _defLogTag;

    private JtFunctionLoader forDebug;
    private JtFunctionLoader forRelease;
    private String _nodeId;

    public JtExecutorAdapter(JtFunctionLoader resourceLoader) {
        _defaultExecutor = JtMapping.getActuator("");
        _defLogTag = "luffy";

        forDebug = new JtFunctionLoaderDebug();
        forRelease = resourceLoader;
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

        if (file == null || file.content == null) {
            file = forRelease.fileGet(path);
        }

        return file;
    }

    @Override
    public List<AFileModel> fileFind(String tag, String label, boolean isCache) throws Exception {
        return Collections.emptyList();
    }

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

    @Override
    public Map cfgMap(String name) throws Exception {
        String val = Solon.cfg().get(name);
        Map tmp = new LinkedHashMap();

        if (val != null) {
            tmp.put("value", val);
            tmp.put("name", name);
            tmp.put("tag", "luffy");
        }

        return tmp;
    }
}
