package features.web.staticfiles;

import org.junit.jupiter.api.Test;
import org.noear.solon.web.staticfiles.StaticMimes;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StaticMimesTest {

    @Test
    public void testMimes() {
        assertEquals("text/html", StaticMimes.findByExt(".html"));
        assertEquals("text/html", StaticMimes.findByExt(".htm"));
        assertEquals("text/css", StaticMimes.findByExt(".css"));
        assertEquals("application/javascript", StaticMimes.findByExt(".js"));
        assertEquals("application/json", StaticMimes.findByExt(".json"));
        assertEquals("image/png", StaticMimes.findByExt(".png"));
        assertEquals("image/jpeg", StaticMimes.findByExt(".jpg"));
        assertNull(StaticMimes.findByExt(".unknown123"));

        assertEquals("text/html", StaticMimes.findByFileName("index.html"));
        assertEquals("text/html", StaticMimes.findByFileName("/path/to/page.htm"));
        assertEquals("image/png", StaticMimes.findByFileName("/static/img.PNG"));

        assertEquals("", StaticMimes.resolveExt("nofileext"));
        assertEquals(".jpg", StaticMimes.resolveExt("photo.jpg"));
        assertEquals(".jpg", StaticMimes.resolveExt("photo.JPG"));
        assertEquals("", StaticMimes.resolveExt("photo.jpg?k=v"));
        assertEquals(".jp", StaticMimes.resolveExt("photo.jpg#anchor"));
        assertEquals("", StaticMimes.resolveExt("folder.name/file"));
        assertEquals("", StaticMimes.resolveExt("test?query"));

        StaticMimes.add(".myext", "application/custom");
        assertEquals("application/custom", StaticMimes.findByExt(".myext"));
        assertEquals("application/custom", StaticMimes.findByFileName("file.myext"));

        Map<String, String> map = StaticMimes.getMap();
        assertNotNull(map);
        assertTrue(map.containsKey(".html"));
        assertThrows(UnsupportedOperationException.class, () -> map.put(".test", "test"));

        new StaticMimes();
    }
}
