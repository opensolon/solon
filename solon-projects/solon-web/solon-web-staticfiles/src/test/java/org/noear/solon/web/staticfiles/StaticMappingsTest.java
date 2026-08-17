package org.noear.solon.web.staticfiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@SolonTest
public class StaticMappingsTest {

    private StaticRepository repo1;
    private StaticRepository repo2;
    private StaticRepository repoRoot;
    private StaticRepository fileRepo;

    @BeforeEach
    public void setup() {
        repo1 = new ClassPathStaticRepository("META-INF/resources/");
        repo2 = new ClassPathStaticRepository("META-INF/resources/webjars/");
        repoRoot = new ClassPathStaticRepository("");
        fileRepo = new ClassPathStaticRepository("META-INF/resources/");
        StaticMappings.locationMap.clear();
    }

    @AfterEach
    public void tearDown() {
        StaticMappings.locationMap.clear();
    }

    @Test
    public void testMappings() throws Exception {
        assertEquals(0, StaticMappings.count());

        StaticMappings.add("/res/", repo1);
        assertEquals(1, StaticMappings.count());

        StaticMappings.add("webjars/", repo2);
        assertEquals(2, StaticMappings.count());

        // repositoryIncPrefix = true
        StaticMappings.addDo("/META-INF/resources/", repoRoot, true);

        // pathPrefixAsFile = true
        StaticMappings.addDo("/doc.html", fileRepo, false);

        URL url1 = StaticMappings.find("/res/doc.html");
        assertNotNull(url1);

        URL url2 = StaticMappings.find("/webjars/hello.html");
        assertNotNull(url2);

        URL url3 = StaticMappings.find("/doc.html");
        assertNotNull(url3);

        URL urlInc = StaticMappings.find("/META-INF/resources/doc.html");
        assertNotNull(urlInc);

        URL url4 = StaticMappings.find("/not_found.html");
        assertNull(url4);

        assertNull(StaticMappings.find("/res/../doc.html"));
        assertNull(StaticMappings.find("/res/..\\doc.html"));
        assertNull(StaticMappings.find("/res/../../doc.html"));
        assertNull(StaticMappings.find("/res/..\\..\\doc.html"));
        assertNull(StaticMappings.find("/res/sub/../../doc.html"));

        StaticMappings.remove(repo1);
        assertNull(StaticMappings.find("/res/doc.html"));

        new StaticMappings();
    }
}
