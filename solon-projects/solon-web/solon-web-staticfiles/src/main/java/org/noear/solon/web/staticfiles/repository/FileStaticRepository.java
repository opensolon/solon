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
package org.noear.solon.web.staticfiles.repository;

import org.noear.solon.web.staticfiles.StaticRepository;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 文件型静态仓库（支持位置例：/user/ 或 file:///user/）
 *
 * @author noear
 * @since 1.5
 */
public class FileStaticRepository implements StaticRepository {
    String location;

    /**
     * 构建函数
     *
     * @param location 位置
     */
    public FileStaticRepository(String location) {
        setLocation(location);
    }

    /**
     * 设置位置
     *
     * @param location 位置
     */
    protected void setLocation(String location) {
        if (location == null) {
            return;
        }

        this.location = location;
    }

    /**
     * @param relativePath 例：demo/file.htm （没有'/'开头）
     * */
    @Override
    public URL find(String relativePath) throws Exception {
        if (location == null || relativePath == null) {
            return null;
        }

        // RFC 3986 percent-decode：path 来自 ctx.pathNew()，rawpath 模式下
        // 可能仍是 %E4%B8%AD%E6%96%87%E6%B5%8B%E8%AF%95 等 UTF-8 转义形式；
        // 注意 '+' 永远是字面量（与 URLDecoder form-encoded 不同），且无 '%' 时
        // 直接返回原字符串避免无谓分配
        String decoded = decodePath(relativePath);

        File baseFile = new File(location).getCanonicalFile();
        File file = new File(baseFile, decoded).getCanonicalFile();

        // 边界防御：目标规范路径必须在基础目录之内且为常规文件
        if (file.getPath().startsWith(baseFile.getPath() + File.separator) || file.equals(baseFile)) {
            if (file.exists() && file.isFile()) {
                return file.toURI().toURL();
            }
        }

        return null;
    }

    /**
     * RFC 3986 percent-decode：'%xx' 解码为字节，其余字符（含 '+'）按字面量原样保留。
     * 与 {@link java.net.URLDecoder} 的 form-encoded 语义不同 —— 后者会把 '+' 转成空格，
     * 这对 URL path 是错误的（RFC 3986 §3.3 path 里 '+' 是 pchar/sub-delim）。
     */
    private static String decodePath(String path) {
        if (path.indexOf('%') < 0) {
            return path;
        }

        int length = path.length();
        byte[] buf = new byte[length];
        int pos = 0;
        for (int i = 0; i < length; i++) {
            char ch = path.charAt(i);
            if (ch == '%' && i + 2 < length) {
                int hi = Character.digit(path.charAt(i + 1), 16);
                int lo = Character.digit(path.charAt(i + 2), 16);
                if (hi >= 0 && lo >= 0) {
                    buf[pos++] = (byte) ((hi << 4) + lo);
                    i += 2;
                    continue;
                }
            }
            buf[pos++] = (byte) ch;
        }
        return new String(buf, 0, pos, StandardCharsets.UTF_8);
    }
}
