package webapp.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String name;
    private int sex;
    private String icon;
    private Date reg_time = new Date();
    private boolean is_ok = false;
}
