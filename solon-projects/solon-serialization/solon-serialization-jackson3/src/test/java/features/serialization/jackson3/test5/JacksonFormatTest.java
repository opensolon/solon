package features.serialization.jackson3.test5;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.jackson3.Jackson3RenderFactory;
import org.noear.solon.test.SolonTest;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author coderWu
 */
@Import(profiles = "classpath:jackson_format_test.yml")
@SolonTest
public class JacksonFormatTest {
    private static final String FORMATTED_DATE = "2024-07-25";

    private static final String FORMATTED_TIME = FORMATTED_DATE + " 12:34:56";

    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    Jackson3RenderFactory renderFactory;

    @Test
    public void customDatePatternTest() throws Throwable {
        TimeModel timeModel = new TimeModel();
        timeModel.setDate(TIME_FORMATTER.parse(FORMATTED_TIME));
        timeModel.setDateWithoutFormat(TIME_FORMATTER.parse(FORMATTED_TIME));

        ContextEmpty ctx = new ContextEmpty();
        renderFactory.create().render(timeModel, ctx);
        String jsonString = ctx.attr("output");
        System.out.println(jsonString);

        assert "{\"date\":\"2024-07-25\",\"dateWithoutFormat\":\"2024-07-25 12:34:56\"}".equals(jsonString);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonObject = objectMapper.readTree(jsonString);

        assertEquals(FORMATTED_DATE, jsonObject.get("date").asText());
        assertEquals(FORMATTED_TIME, jsonObject.get("dateWithoutFormat").asText());
    }

    @Data
    private static class TimeModel {
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private Date date;

        private Date dateWithoutFormat;
    }
}
