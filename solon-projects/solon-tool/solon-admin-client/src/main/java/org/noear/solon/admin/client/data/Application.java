package org.noear.solon.admin.client.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
public class Application {

    private final String name;

    private String baseUrl;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final String metadata;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final boolean showSecretInformation;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final EnvironmentInformation environmentInformation;

}
