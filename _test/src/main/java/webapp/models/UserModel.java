package webapp.models;

import org.noear.solon.annotation.XParam;

import java.util.Date;

public class UserModel {
    public int id;
    public String name;
    public int sex;

    public transient String _type;

    @XParam("yyyy-MM-dd")
    public Date date;

    public long[] aaa;
}
