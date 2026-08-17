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
package features.web.staticfiles;

import org.junit.jupiter.api.Test;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author noear 2022/7/11 created
 */
public class PathTest {
    @Test
    public void test() {
        File file = new File("upload", "xxx.jpg");
        System.out.println(file.toURI());
    }

    @Test
    public void testPathTraversalDefense() throws Exception {
        // 创建临时测试目录与文件
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_static_test_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        File secretFile = new File(tempDir.getParentFile(), "secret_" + System.currentTimeMillis() + ".txt");
        secretFile.createNewFile();
        secretFile.deleteOnExit();

        File normalFile = new File(tempDir, "test.txt");
        normalFile.createNewFile();
        normalFile.deleteOnExit();

        FileStaticRepository repo = new FileStaticRepository(tempDir.getAbsolutePath());

        // 1. 正常访问测试
        URL normalUrl = repo.find("test.txt");
        assertNotNull(normalUrl);

        // 2. 路径穿越攻击测试（../跳出根目录）
        URL hackUrl = repo.find("../" + secretFile.getName());
        assertNull(hackUrl, "Path traversal with ../ should be blocked and return null");

        // Windows反斜杠路径穿越（..\）
        URL winHackUrl = repo.find("..\\" + secretFile.getName());
        assertNull(winHackUrl, "Path traversal with ..\\ should be blocked and return null");

        // 多级路径穿越
        URL multiHackUrl = repo.find("../../" + secretFile.getName());
        assertNull(multiHackUrl, "Multi-level path traversal should be blocked and return null");

        // 包含在子目录中的越界穿越
        URL subHackUrl = repo.find("sub/../../" + secretFile.getName());
        assertNull(subHackUrl, "Sub-level path traversal should be blocked and return null");

        // 3. 空路径/null参数
        assertNull(repo.find(null));
        assertNull(repo.find(""));

        // 4. 目录访问（不应作为普通文件返回）
        assertNull(repo.find("/"));
        assertNull(repo.find("."));
    }
}

