package org.noear.solon.ai.search;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author noear 2025/2/14 created
 */
public interface SearchApi {
    SearchResponse webSearch(SearchRequest request);

    default SearchResponse webSearch(String query) {
        return webSearch(SearchRequest.builder()
                .query(query)
                .build());
    }

    /***/
    default String format(String query, SearchResponse.Value[] values) {
        if (Objects.isNull(values) || values.length == 0) {
            return null;
        }

        String results = Arrays
                .stream(values).map(
                        organicResult -> "Title: " + organicResult.getName() + "\n" + "Source: "
                                + organicResult.getUrl() + "\n"
                                + (organicResult.getSummary() != null ? "Content:" + "\n" + organicResult.getSummary()
                                : "Snippet:" + "\n" + organicResult.getSnippet()))
                .collect(Collectors.joining("\n\n"));

        return String.format("用户输入提问: %s\n\n 当前时间:%s \n\n 参考如下内容进行推理回答:%s", query,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), results);
    }
}
