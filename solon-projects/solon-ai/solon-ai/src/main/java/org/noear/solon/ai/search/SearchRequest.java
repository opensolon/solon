package org.noear.solon.ai.search;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

/**
 * @author noear 2025/2/14 created
 */
@Builder
@Data
public class SearchRequest {
    private final boolean enable;

    @Setter
    private String query;

    @Builder.Default
    private final FreshnessEnums freshness = FreshnessEnums.NO_LIMIT;

    @Builder.Default
    private final boolean summary = true;

    @Builder.Default
    private final int count = 10;

    @Builder.Default
    private final int page = 1;
}
