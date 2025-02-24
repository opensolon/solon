package features.ai.chat;

import org.noear.solon.ai.chat.annotation.FunctionMapping;
import org.noear.solon.ai.chat.annotation.FunctionParam;
import org.noear.solon.annotation.Component;
import org.noear.solon.net.http.HttpUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author noear 2025/2/6 created
 */
@Component
public class Tools {
    @FunctionMapping(description = "获取指定城市的天气情况")
    public String get_weather(@FunctionParam(name = "location", description = "根据用户提到的地点推测城市") String location) {
        if (location == null) {
            throw new IllegalStateException("arguments location is null (Assistant recognition failure)");
        }

        return "晴，24度";// + weatherService.get(location);
    }

    @FunctionMapping(description = "用关键词搜索网络")
    public String search_www(@FunctionParam(name = "key", description = "根据用户内容提取关键词") String key) throws IOException {
        if (key == null) {
            throw new IllegalStateException("arguments key is null (Assistant recognition failure)");
        }

//        return Prompts.augment(key, Document.builder()
//                .title("概述")
//                .url("https://solon.noear.org/article/about").build())
//                .getContent();

        return HttpUtils.http("https://solon.noear.org/article/about?format=md").get();
    }
}