package cn.afterturn.easypoi.wps.entity.resreq;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
public class WpsFileHistoryRequest extends WpsResponse implements Serializable {
    private String id;
    private int    offset;
    private int    count;

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOffset(){
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount(){
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
