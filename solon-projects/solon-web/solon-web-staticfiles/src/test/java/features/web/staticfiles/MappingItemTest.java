package features.web.staticfiles;

import org.junit.jupiter.api.Test;
import org.noear.solon.web.staticfiles.model.MappingItem;

import static org.junit.jupiter.api.Assertions.*;

public class MappingItemTest {

    @Test
    public void testMappingItem() {
        MappingItem item = new MappingItem();
        item.setPath("/static/**");
        item.setRepository("classpath:static/");

        assertEquals("/static/**", item.getPath());
        assertEquals("classpath:static/", item.getRepository());
    }
}
