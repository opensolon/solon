package cn.afterturn.easypoi.wps.entity.resreq;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
public class WpsRenameRequest extends WpsResponse implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
