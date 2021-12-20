package webapp.models;

import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {
    public int id;
    public String name;
    public int sex;

    public transient String _type;

    public Date date = new Date();

    public long[] aaa;
}
