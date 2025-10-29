package features.serialization.jackson.props;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 *
 * @author noear 2025/10/29 created
 *
 */
public class PropsTest {
    @Test
    public void case1() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("user.name", "s");
        properties.setProperty("user.tags[0]", "a");
        properties.setProperty("user.tags[2]", "b");

        ObjectMapper mapper = new ObjectMapper();
        String tmp = mapper.writeValueAsString(properties);
        System.out.println(tmp);
    }
}
