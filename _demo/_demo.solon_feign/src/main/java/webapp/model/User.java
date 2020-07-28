package webapp.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class User {
    private String name;
    private int sex;
    private String icon;
    private Date reg_time;
    private boolean state;
    private List<String> orderList;
}
