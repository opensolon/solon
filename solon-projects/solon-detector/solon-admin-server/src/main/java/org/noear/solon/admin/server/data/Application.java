package org.noear.solon.admin.server.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

/**
 * 应用程序数据
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Data
@NoArgsConstructor
public class Application {

    private String name;

    private String token;

    private String baseUrl;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String metadata;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Status status = Status.DOWN;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private long startupTime = System.currentTimeMillis();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private long lastHeartbeat;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private long lastUpTime;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private long lastDownTime;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private boolean showSecretInformation;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EnvironmentInformation environmentInformation;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Collection<Detector> monitors;

    public void replace(Application application) {
        this.metadata = application.metadata;
        this.status = application.status;
        this.startupTime = application.startupTime;
        this.lastHeartbeat = application.lastHeartbeat;
        this.lastUpTime = application.lastUpTime;
        this.lastDownTime = application.lastDownTime;
        this.showSecretInformation = application.showSecretInformation;
        this.environmentInformation = application.environmentInformation;
        this.monitors = application.monitors;
    }

    public enum Status {
        UP,
        DOWN
    }

    public String toKey() {
        return name + "@" + baseUrl;
    }
}
