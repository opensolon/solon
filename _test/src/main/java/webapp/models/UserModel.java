package webapp.models;

import org.noear.solon.annotation.Param;

import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {
    public int id;
    public String name;
    public int sex;

    public transient String _type;


    @Param(format = "yyyy-MM-dd")
    public Date date = new Date();

    public long[] aaa;
}
