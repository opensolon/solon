package webapp.demoz_tool;

import lombok.Data;
import org.noear.solon.annotation.BindProps;

import java.util.ArrayList;
import java.util.List;

@Data
@BindProps(prefix = "demo")
public class DemoProps {

    private int port;

    private Integer intValue;

    private Long longValue;

    private String stringValue;

    private Boolean booleanValue;

    private Float floatValue;

    private Double doubleValue;

    private Byte byteValue;

    private Short shortValue;

    private Character charValue;

    private String[] stringArrayValue;

    private List<String> stringListValue;

    private DemoEnum demoEnum;

    private DemoEnumWithValue demoEnumWithValue;

    private Boolean defaultValue = true;

    private String[] defaultStringArrayValue = new String[]{"a", "b"};

    private List<String> defaultListStringValue = new ArrayList<String>() {{
        add("a");
        add("b");
    }};

    private DemoEnum defaultDemoEnum = DemoEnum.A;

    private DemoEnumWithValue defaultDemoEnumWithValue = DemoEnumWithValue.B;
}
