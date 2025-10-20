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

import org.noear.luffy.model.AFileModel;
import org.noear.snack4.ONode;

/**
 * 模型工具
 *
 * @author noear
 * @since 3.4
 */
public class JtModelUtils {
    /**
     * 构建文件模型
     */
    public static void buildFileModel(AFileModel fileModel, String path) {
        //有些场景需要id
        fileModel.file_id = Math.abs(path.hashCode());

        fileModel.path = path;
        fileModel.tag = "luffy";

        String fileName = getFileName(path);
        if (fileName.indexOf('.') > 0) {
            String suffix = fileName.substring(fileName.indexOf('.') + 1);
            fileModel.edit_mode = JtMapping.getActuator(suffix);
        } else {
            fileModel.edit_mode = JtMapping.getActuator("");
        }

        int headIdx = fileModel.content.indexOf('\n');
        if (headIdx > 0) {
            String headLine = fileModel.content.substring(0, headIdx);
            if (headLine.startsWith("//@{")) { //经典注释风格
                String metaJson = headLine.substring(3);
                ONode metaNode = ONode.ofJson(metaJson);
                fileModel.method = metaNode.get("method").getString();
            } else if (headLine.startsWith("#@{")) { //像 py、rb 注释风格
                String metaJson = headLine.substring(2);
                ONode metaNode = ONode.ofJson(metaJson);
                fileModel.method = metaNode.get("method").getString();
            }
        }
    }

    private static String getFileName(String path) {
        int idx = path.lastIndexOf('/');
        if (idx < 0) {
            return path;
        } else {
            return path.substring(idx + 1);
        }
    }
}
