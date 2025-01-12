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
package org.noear.solon.web.webdav;

import java.io.InputStream;
import java.util.List;

/**
 * webdav文件系统
 *
 * @author 阿范
 */
public interface FileSystem {
    FileInfo fileInfo(String reqPath);

    String fileMime(FileInfo fi);

    List<FileInfo> fileList(String reqPath);

    String findEtag(String reqPath, FileInfo fi);

    InputStream fileInputStream(String reqPath, long start, long length);

    boolean putFile(String reqPath, InputStream in);

    boolean del(String reqPath);

    boolean copy(String reqPath, String descPath);

    boolean move(String reqPath, String descPath);

    boolean mkdir(String reqPath);

    String fileUrl(String reqPath);
}
