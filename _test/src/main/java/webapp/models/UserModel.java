package webapp.models;

import org.noear.solon.annotation.Param;

import java.util.Date;

public class UserModel {
    public int id;
    public String name;
    public int sex;

    public transient String _type;

    public Date date = new Date();

    public long[] aaa;
}
