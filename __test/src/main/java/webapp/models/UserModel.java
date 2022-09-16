package webapp.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserModel implements Serializable {
    private int id;
    private String name;
    private int sex;

    private transient String _type;

    private Date date = new Date();

    private long[] aaa;

}
