package cn.afterturn.easypoi.wps.entity.resreq;

import cn.afterturn.easypoi.wps.entity.WpsFileHistoryEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author jueyue on 20-5-8.
 */
public class WpsFileHistoryResponse extends WpsResponse implements Serializable {

    private List<WpsFileHistoryEntity> histories;

    public List<WpsFileHistoryEntity> getHistories() {
        return histories;
    }

    public void setHistories(List<WpsFileHistoryEntity> histories) {
        this.histories = histories;
    }
}
