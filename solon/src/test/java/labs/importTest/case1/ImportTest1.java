package labs.importTest.case1;

import labs.importTest.DemoCom1;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Import;

/**
 * @author noear 2023/10/18 created
 */
@Import(value = DemoCom1.class, propertySource = "classpath:demo.properties")
public class ImportTest1 {
    public static void main(String[] args) {
        Solon.start(ImportTest1.class, args);

        assert "a".equals(Solon.cfg().get("demo.name"));
    }
}
