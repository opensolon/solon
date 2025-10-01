package webapp.demoz_tool;

import lombok.Data;

import java.util.List;

@Data
public class AConfig {
    private String name;

    private Boolean defaultValue = true;

    private String[] arrays;

    private List<Long> ids;
}
