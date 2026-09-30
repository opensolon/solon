package features.web.staticfiles;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.test.SolonTest;
import org.noear.solon.web.staticfiles.StaticLocation;
import org.noear.solon.web.staticfiles.StaticRepository;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
import org.noear.solon.web.staticfiles.repository.ExtendStaticRepository;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@SolonTest
public class RepositoryTest {

    @Test
    public void testClassPathStaticRepository() throws Exception {
        ClassPathStaticRepository repo1 = new ClassPathStaticRepository("META-INF/resources");
        URL url1 = repo1.find("doc.html");
        assertNotNull(url1);

        ClassPathStaticRepository repo2 = new ClassPathStaticRepository(RepositoryTest.class.getClassLoader(), "/META-INF/resources/");
        URL url2 = repo2.find("doc.html");
        assertNotNull(url2);

        assertNull(repo1.find("not_exist.html"));
        assertNull(repo1.find(null));

        ClassPathStaticRepository nullRepo = new ClassPathStaticRepository((String) null);
        assertNull(nullRepo.find("doc.html"));

        repo1.preheat("doc.html", true);
        repo1.preheat("doc.html", false);
        repo1.preheat("not_exist.html", true);
    }

    @Test
    public void testClassPathStaticRepositoryInDebugMode() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_cp_debug_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        File docFile = new File(tempDir, "test_debug.html");
        docFile.createNewFile();
        docFile.deleteOnExit();

        File subDir = new File(tempDir, "sub");
        subDir.mkdirs();
        subDir.deleteOnExit();

        ClassPathStaticRepository repo = new ClassPathStaticRepository("META-INF/resources");

        Field locDebugField = ClassPathStaticRepository.class.getDeclaredField("locationDebug");
        locDebugField.setAccessible(true);
        locDebugField.set(repo, tempDir);

        URL found = repo.find("test_debug.html");
        assertNotNull(found);

        assertNull(repo.find("sub"));
        assertNull(repo.find("../secret.txt"));
    }

    @Test
    public void testFileStaticRepository() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_repo_test_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        File file1 = new File(tempDir, "hello.txt");
        file1.createNewFile();
        file1.deleteOnExit();

        FileStaticRepository repo = new FileStaticRepository(tempDir.getAbsolutePath());
        assertNotNull(repo.find("hello.txt"));
        assertNull(repo.find("non_exist.txt"));
        assertNull(repo.find(null));

        FileStaticRepository nullRepo = new FileStaticRepository(null);
        assertNull(nullRepo.find("hello.txt"));

        repo.preheat("hello.txt", false);
    }

    /**
     * IJMHZ4：rawpath 模式下 ctx.pathNew() 会带 %xx 转义，FileStaticRepository
     * 必须按 RFC 3986 percent-decode 后再落盘查找。覆盖中/空/井号/数字/正负号等。
     */
    @Test
    public void testFileStaticRepositoryPercentDecode() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_repo_decode_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        // 中英混合
        File cn = new File(tempDir, "中文测试.txt");
        writeUtf8(cn, "cn");
        // 空格
        File space = new File(tempDir, "edge space.txt");
        writeUtf8(space, "sp");
        // # 号（RFC 3986 fragment 分隔符；server 端不应传过来，但作为文件名字面是合法的）
        File hash = new File(tempDir, "edge#hash.txt");
        writeUtf8(hash, "ha");
        // 数字
        File num = new File(tempDir, "file123.txt");
        writeUtf8(num, "nu");
        // 四合一：中文 + 空格 + # + 数字（IJMHZ4 描述的具体 case）
        File combo = new File(tempDir, "中文 测试 123 #.txt");
        writeUtf8(combo, "co");
        // 只含空格的对照组文件（用来验证 '+' 不被当空格用 —— URLDecoder 会把 '+' 当 ' '）
        File spaceOnly = new File(tempDir, "a b.txt");
        writeUtf8(spaceOnly, "so");
        // 真·含字面 '+' 的文件（验证 RFC 3986 把 '+' 当字面量能命中）
        File plus = new File(tempDir, "a+b.txt");
        writeUtf8(plus, "pl");

        FileStaticRepository repo = new FileStaticRepository(tempDir.getAbsolutePath());

        // 收集所有断言失败，一次性暴露（JUnit 默认首个失败就停，掩盖其他 bug）
        java.util.List<String> failures = new java.util.ArrayList<>();
        check(failures, () -> assertNotNull(repo.find("中文测试.txt"),         "decoded 中文"));
        check(failures, () -> assertNotNull(repo.find("edge space.txt"),       "decoded space"));
        check(failures, () -> assertNotNull(repo.find("edge#hash.txt"),        "decoded hash"));
        check(failures, () -> assertNotNull(repo.find("file123.txt"),          "decoded digits"));
        check(failures, () -> assertNotNull(repo.find("中文 测试 123 #.txt"),  "decoded combo"));
        check(failures, () -> assertNotNull(repo.find("a+b.txt"),              "decoded plus (literal)"));

        // raw 形式（IJMHZ4 修复目标）
        check(failures, () -> assertNotNull(repo.find("%E4%B8%AD%E6%96%87%E6%B5%8B%E8%AF%95.txt"),         "raw 中文"));
        check(failures, () -> assertNotNull(repo.find("edge%20space.txt"),                              "raw space"));
        check(failures, () -> assertNotNull(repo.find("%E4%B8%AD%E6%96%87%20%E6%B5%8B%E8%AF%95%20123%20%23.txt"), "raw combo"));
        check(failures, () -> assertNotNull(repo.find("a%2Bb.txt"),                                     "raw plus via %2B"));

        // '+' 必须是字面量 —— 不当空格
        check(failures, () -> assertNotNull(repo.find("a b.txt"),   "control: space-only file is findable"));
        check(failures, () -> assertNotNull(repo.find("a+b.txt"),  "RFC 3986: '+' literal, hits 'a+b.txt'"));
        check(failures, () -> assertNotNull(repo.find("a%2Bb.txt"), "raw %2B decodes to literal '+'"));

        assertTrue(failures.isEmpty(), String.join("\n", failures));
    }

    /**
     * IJMHZ4 安全防御 + Spring 对照：decode 后路径不能越出 base 目录，
     * 同时 pin 与 Spring 框架一致的安全语义。
     *
     * <p>参照 Spring 测试：
     * <ul>
     *   <li>PathResourceResolverTests.checkResource —— hex 大小写都拒（%2E%2E / %2e%2e）</li>
     *   <li>PathResourceResolverTests.ignoreInvalidEscapeSequence (gh-23463) —— %foo% 不抛</li>
     *   <li>ResourceHttpRequestHandlerTests.shouldRejectPathWithTraversal —— 双重编码 %2F%2F%2E%2E%2F%2F</li>
     *   <li>UriUtilsTests.decode —— idempotent（无 % 时原样返回）</li>
     * </ul>
     */
    @Test
    public void testFileStaticRepositoryPercentDecodeSecurity() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_repo_decode_sec_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        FileStaticRepository repo = new FileStaticRepository(tempDir.getAbsolutePath());

        java.util.List<String> failures = new java.util.ArrayList<>();

        // ---- IJMHZ4 安全防御 ----

        // 已存在文件不应被 ../ 解码后越界命中（path 里含 ../ 在 StaticMappings 层就被拦，
        // 这里直接给 FileStaticRepository 也走一次：解码后的相对路径不应逃出 baseDir）
        check(failures, () -> assertNull(repo.find("..%2Fetc%2Fpasswd"),
                "raw ../ must be decoded but still bounded by baseDir"));
        check(failures, () -> assertNull(repo.find("%2E%2E%2Fsecret.txt"),
                "raw ../ must not escape baseDir"));

        // 截断的 %xx（落盘自然找不到，但不应抛异常）
        check(failures, () -> assertNull(repo.find("foo%2.txt"),
                "truncated %xx should not throw"));

        // ---- Spring 对照（PathResourceResolver 行为等级）----

        // Hex 大小写不敏感：必须同时拒绝 %2E%2E 和 %2e%2e
        // （Spring PathResourceResolverTests.checkResource 显式测两条）
        check(failures, () -> assertNull(repo.find("%2E%2E/testsecret/secret.txt"),
                "uppercase %2E%2E rejected"));
        check(failures, () -> assertNull(repo.find("%2e%2e/testsecret/secret.txt"),
                "lowercase %2e%2e rejected"));

        // 双重编码（ResourceHttpRequestHandlerTests.shouldRejectPathWithTraversal）：
        // %2F%2F%2E%2E%2F%2F 解码一次后是 //../\/\，仍含 ../，必须被 baseDir 防御拦下
        check(failures, () -> assertNull(repo.find("%2F%2F%2E%2E%2F%2Ftestsecret/secret.txt"),
                "double-encoded traversal rejected"));

        // ignoreInvalidEscapeSequence (gh-23463)：%foo% 不抛 IAE，落盘找不到返回 null
        // Spring 在 PathResourceResolver 层容忍，在 UriUtils 层严格抛 —— 我们这层
        // 等价 PathResourceResolver 层，必须容忍
        check(failures, () -> assertNull(repo.find("%foo%/bar.txt"),
                "%foo% tolerated (no IAE), file not found"));
        check(failures, () -> assertNull(repo.find("test%file.txt"),
                "test%file tolerated (no IAE), file not found"));

        // Idempotency（UriUtilsTests.decode foobar -> foobar）：
        // 无 % 时原样返回 —— fast-path 已隐含
        check(failures, () -> assertNull(repo.find("foobar"),
                "idempotent: 'foobar' decodes to itself, no such file"));

        assertTrue(failures.isEmpty(), String.join("\n", failures));
    }

    /**
     * 字符集与编码矩阵：覆盖 4-byte UTF-8（emoji/吉祥物）、大小写 hex、2-byte UTF-8、混合
     * encoded+literal、BOM、编码斜杠 %2F、无效 UTF-8、双重编码、malformed %。
     * 这些 case 都应 PASS —— 用来 pin 当前行为契约，防止未来"优化"悄悄改变语义。
     */
    @Test
    public void testFileStaticRepositoryPercentDecodeCharsetMatrix() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_repo_charset_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        // 4-byte UTF-8：🚀 = U+1F680 = %F0%9F%9A%80
        File emoji = new File(tempDir, "🚀.txt");
        writeUtf8(emoji, "em");
        // 2-byte UTF-8：é = U+00E9 = %C3%A9
        File latin = new File(tempDir, "café.txt");
        writeUtf8(latin, "la");
        // BOM 文件名（极少见，但合法）：%EF%BB%BF.txt
        File bom = new File(tempDir, "﻿.txt");
        writeUtf8(bom, "bm");
        // 混合 encoded + literal："prefix %20 suffix.txt" 解码后是 "prefix   suffix.txt"
        File mixed = new File(tempDir, "prefix   suffix.txt");
        writeUtf8(mixed, "mx");
        // 给 hex 大小写测试用：%e4%b8%ad%e6%96%87 = "中文"（两个汉字）
        File cn2 = new File(tempDir, "中文.txt");
        writeUtf8(cn2, "cn");

        FileStaticRepository repo = new FileStaticRepository(tempDir.getAbsolutePath());

        java.util.List<String> failures = new java.util.ArrayList<>();

        // 4-byte UTF-8（RFC 3986 §2.5：percent-encoding 在 octet 层操作，多字节就是 N 个连续 %xx）
        check(failures, () -> assertNotNull(repo.find("%F0%9F%9A%80.txt"), "raw 4-byte UTF-8 emoji"));
        check(failures, () -> assertNotNull(repo.find("🚀.txt"),       "decoded emoji control"));

        // 2-byte UTF-8（"看起来像 Latin-1，实际是 UTF-8"）
        check(failures, () -> assertNotNull(repo.find("caf%C3%A9.txt"), "raw 2-byte UTF-8 é"));
        check(failures, () -> assertNotNull(repo.find("café.txt"),     "decoded é control"));

        // 大小写 hex 混合：%e4 vs %E4 —— RFC 3986 §2.1 强制 parser 两种都接受
        check(failures, () -> assertNotNull(repo.find("%e4%B8%ad%E6%96%87.txt"), "lowercase hex digits accepted"));
        // 全小写（注意：%e4%b8%ad%e6%96%87 = "中文" 两个汉字，与磁盘文件 "中文.txt" 对应）
        check(failures, () -> assertNotNull(repo.find("%e4%b8%ad%e6%96%87.txt"),  "all lowercase hex"));

        // BOM（%EF%BB%BF）
        check(failures, () -> assertNotNull(repo.find("%EF%BB%BF.txt"), "raw UTF-8 BOM"));
        check(failures, () -> assertNotNull(repo.find("﻿.txt"),    "decoded BOM control"));

        // 混合 encoded + literal：解码后是 "prefix   suffix.txt"（三个空格 = 一个原字面空格 + 一个 %20）
        check(failures, () -> assertNotNull(repo.find("prefix %20 suffix.txt"), "mixed encoded + literal"));
        check(failures, () -> assertNotNull(repo.find("prefix   suffix.txt"),   "decoded mixed control"));

        // 编码斜杠 %2F：解码后是字面 '/'，会触发 baseDir 边界防御 —— 必须 null（防御深度）
        check(failures, () -> assertNull(repo.find("foo%2Fbar.txt"),
                "raw %2F decodes to '/' — substring check must still catch it"));

        // 双重编码 %2520：%25 解码为字面 '%'，剩下 "20" 是字面字符 —— 总结果 "%20"
        // pin 当前行为：decodePath 只解一次，不会 double-decode 成 ' '
        File doubleDecoded = new File(tempDir, "%20.txt"); // 文件名是字面 %20
        writeUtf8(doubleDecoded, "dd");
        check(failures, () -> assertNotNull(repo.find("%2520.txt"),
                "double-encoded %2520 decodes once to '%20' (RFC silent, doc'd behavior)"));

        // Malformed %：保留字面 %
        //  - "%" 在末尾：i+2 越界，走 fallthrough 写 '%'
        //  - "%X" 非 hex：Character.digit 返回 -1，走 fallthrough 写 '%X'
        check(failures, () -> assertNull(repo.find("foo%.txt"),     "trailing % preserved, file not found"));
        check(failures, () -> assertNull(repo.find("foo%XY.txt"),   "non-hex %XY preserved, file not found"));

        assertTrue(failures.isEmpty(), String.join("\n", failures));
    }

    private static void writeUtf8(File f, String s) throws Exception {
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(f)) {
            fos.write(s.getBytes("UTF-8"));
        }
    }

    /**
     * 把单条断言失败收集到 {@code failures} 里而不是立刻抛 —— 让一次跑测能完整暴露所有 bug。
     */
    @FunctionalInterface
    private interface CheckedAssertion {
        void run() throws Throwable;
    }

    private static void check(java.util.List<String> failures, CheckedAssertion a) {
        try {
            a.run();
        } catch (Throwable t) {
            failures.add(t.getMessage());
        }
    }

    @Test
    public void testExtendStaticRepository() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "solon_extend_test_" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        File staticFolder = new File(tempDir, "static");
        staticFolder.mkdirs();
        staticFolder.deleteOnExit();

        File extFile = new File(staticFolder, "ext.txt");
        extFile.createNewFile();
        extFile.deleteOnExit();

        ExtendLoader.load(tempDir.getAbsolutePath(), false);

        ExtendStaticRepository repo = new ExtendStaticRepository();
        URL url = repo.find("ext.txt");
        assertNotNull(url);
        assertNull(repo.find("not_exist.txt"));
        assertNull(repo.find(null));
        assertNull(repo.find("../secret.txt"));

        Field locField = ExtendStaticRepository.class.getDeclaredField("location");
        locField.setAccessible(true);
        locField.set(repo, null);
        assertNull(repo.find("ext.txt"));

        locField.set(repo, staticFolder);
        repo.preheat("ext.txt", false);
    }

    @Test
    public void testStaticLocation() {
        StaticRepository dummy = relativePath -> null;
        StaticLocation loc1 = new StaticLocation("/static/", dummy, false);
        assertEquals("/static/", loc1.pathPrefix);
        assertFalse(loc1.pathPrefixAsFile);
        assertSame(dummy, loc1.repository);
        assertFalse(loc1.repositoryIncPrefix);

        StaticLocation loc2 = new StaticLocation("/doc.html", dummy, true);
        assertEquals("/doc.html", loc2.pathPrefix);
        assertTrue(loc2.pathPrefixAsFile);
        assertSame(dummy, loc2.repository);
        assertTrue(loc2.repositoryIncPrefix);

        StaticLocation loc3 = new StaticLocation("/doc.html.gz", dummy, true);
        assertEquals("/doc.html.gz", loc3.pathPrefix);
        assertTrue(loc3.pathPrefixAsFile);
        assertSame(dummy, loc3.repository);
        assertTrue(loc3.repositoryIncPrefix);

        StaticLocation loc4 = new StaticLocation("/doc.html.br", dummy, true);
        assertEquals("/doc.html.br", loc4.pathPrefix);
        assertTrue(loc4.pathPrefixAsFile);
        assertSame(dummy, loc4.repository);
        assertTrue(loc4.repositoryIncPrefix);
    }
}
