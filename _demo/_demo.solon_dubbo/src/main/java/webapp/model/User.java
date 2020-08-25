package webapp.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class User {
    private String name;
    private int sex;
    private String icon;
    private Date regTime;
    private boolean state;
    private List<String> orderList;
    private Map<String,Object> attrMap;
}
