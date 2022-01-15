package features.cache;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author noear 2022/1/15 created
 */
@Data
public class Oauth implements Serializable {
    private Integer id;
    private String code;
    private String url;
    private LocalDateTime expTime;
}
