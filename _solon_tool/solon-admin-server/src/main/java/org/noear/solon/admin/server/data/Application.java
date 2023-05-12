package org.noear.solon.admin.server.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@lombok.ToString(exclude = "metadata")
@EqualsAndHashCode(exclude = "metadata")
@NoArgsConstructor
public class Application {

    private String name;

    private String baseUrl;

    private String metadata;

}
