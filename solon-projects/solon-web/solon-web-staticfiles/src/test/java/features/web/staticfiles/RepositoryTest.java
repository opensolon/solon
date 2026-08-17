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
    }
}
