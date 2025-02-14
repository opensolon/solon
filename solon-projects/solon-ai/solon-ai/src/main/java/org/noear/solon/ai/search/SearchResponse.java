package org.noear.solon.ai.search;

import lombok.Data;

/**
 * @author noear 2025/2/14 created
 */
@Data
public class SearchResponse {
    private Integer code;

    private String log_id;

    private String msg;

    private ResponsData data;

    @Data
    public static class ResponsData {

        private String _type;

        private QueryContext queryContext;

        private WebPages webPages;

        private Images images;

    }

    @Data
    public static class QueryContext {

        private String originalQuery;

    }

    @Data
    public static class WebPages {

        private String webSearchUrl;

        private Integer totalEstimatedMatches;

        private Value[] value;

        private Boolean someResultsRemoved;

    }

    @Data
    public static class Value {

        private String id;

        private String name;

        private String url;

        private String displayUrl;

        private String snippet;

        private String summary;

        private String siteName;

        private String siteIcon;

        private String dateLastCrawled;

        private String cachedPageUrl;

        private String language;

        private Boolean isFamilyFriendly;

        private Boolean isNavigational;

    }

    @Data
    public static class Images {

        private String id;

        private String readLink;

        private String webSearchUrl;

        private Value[] value;

        private Boolean isFamilyFriendly;

    }
}
