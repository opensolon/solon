package cn.afterturn.easypoi.wps.entity.resreq;

import cn.afterturn.easypoi.wps.entity.WpsUserEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jueyue on 20-5-8.
 */
public class WpsUserResponse extends WpsResponse implements Serializable {

    private List<WpsUserEntity> users = new ArrayList<>();

    public void setUsers(List<WpsUserEntity> users) {
        this.users = users;
    }

    public List<WpsUserEntity> getUsers() {
        return users;
    }
}
