package org.noear.solon.admin.server.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(of = {"name", "baseUrl"})
@EqualsAndHashCode(of = {"name", "baseUrl"})
@NoArgsConstructor
public class Application {

    private String name;

    private String baseUrl;

    private String metadata;

    private Status status = Status.DOWN;

    private long startupTime = System.currentTimeMillis();

    private long lastHeartbeat;

    private long lastUpTime;

    private long lastDownTime;

    public enum Status {
        @SerializedName("0")
        UP,
        @SerializedName("1")
        DOWN
    }

}
