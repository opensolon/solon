package features.web.staticfiles;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AppContext;
import org.noear.solon.test.SolonTest;
import org.noear.solon.web.staticfiles.StaticConfig;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.StaticMimes;
import org.noear.solon.web.staticfiles.integration.WebStaticfilesPlugin;

import static org.junit.jupiter.api.Assertions.*;

@SolonTest
public class PluginTest {

    @Test
    public void testPluginStart() throws Exception {
        AppContext context = Solon.context();

        // 1. 测试常规启动
        WebStaticfilesPlugin plugin = new WebStaticfilesPlugin();
        plugin.start(context);

        assertNotNull(StaticMimes.findByExt(".vue"));
        assertNotNull(StaticMimes.findByExt(".map"));
        assertNotNull(StaticMimes.findByExt(".log"));
    }

    @Test
    public void testPluginDisabledByApp() {
        SolonApp app = Solon.app();
        app.enableStaticfiles(false);
        WebStaticfilesPlugin plugin = new WebStaticfilesPlugin();
        plugin.start(app.context());
        app.enableStaticfiles(true);
    }

    @Test
    public void testPluginDisabledByConfig() {
        SolonApp app = Solon.app();
        app.enableStaticfiles(true);
        Solon.cfg().setProperty(StaticConfig.PROP_ENABLE, "false");
        WebStaticfilesPlugin plugin = new WebStaticfilesPlugin();
        plugin.start(app.context());
        Solon.cfg().setProperty(StaticConfig.PROP_ENABLE, "true");
    }

    @Test
    public void testPluginWithMappingsConfig() {
        SolonApp app = Solon.app();
        app.enableStaticfiles(true);

        Solon.cfg().setProperty("solon.staticfiles.mappings[0].path", "public");
        Solon.cfg().setProperty("solon.staticfiles.mappings[0].repository", "classpath:META-INF/resources/");

        Solon.cfg().setProperty("solon.staticfiles.mappings[1].path", "/files");
        Solon.cfg().setProperty("solon.staticfiles.mappings[1].repository", "file:/tmp/");

        Solon.cfg().setProperty("solon.staticfiles.mappings[2].path", "/ext/");
        Solon.cfg().setProperty("solon.staticfiles.mappings[2].repository", ":");

        Solon.cfg().setProperty("solon.staticfiles.mappings[3].path", "/plain/");
        Solon.cfg().setProperty("solon.staticfiles.mappings[3].repository", "/var/data/");

        Solon.cfg().setProperty("solon.staticfiles.mappings[4].path", "");
        Solon.cfg().setProperty("solon.staticfiles.mappings[4].repository", "");

        Solon.cfg().setProperty("solon.staticfiles.mappings[5].path", "/nullrepo/");
        Solon.cfg().setProperty("solon.staticfiles.mappings[5].repository", "");

        Solon.cfg().setProperty("solon.mime.mapping.custom1", "text/custom1");
        Solon.cfg().setProperty("solon.mime.mytest", "text/mytest");

        WebStaticfilesPlugin plugin = new WebStaticfilesPlugin();
        plugin.start(app.context());

        assertEquals("text/custom1", StaticMimes.findByExt(".custom1"));
        assertEquals("text/mytest", StaticMimes.findByExt(".mytest"));
        assertTrue(StaticMappings.count() > 0);
    }
}
