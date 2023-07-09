package org.noear.solon.admin.server.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Data
@NoArgsConstructor
public class Application {

    private String name;

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

    public enum Status {
        @SerializedName("0")
        UP,
        @SerializedName("1")
        DOWN
    }

}
