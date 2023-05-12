package org.noear.solon.admin.client.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@lombok.ToString(exclude = "metadata")
public class Application {

    private final String name;

    private String baseUrl;

    private final String metadata;

}
