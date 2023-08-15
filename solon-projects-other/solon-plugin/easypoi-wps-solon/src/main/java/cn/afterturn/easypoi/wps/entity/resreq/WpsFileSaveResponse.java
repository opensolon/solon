package cn.afterturn.easypoi.wps.entity.resreq;

import cn.afterturn.easypoi.wps.entity.WpsFileEntity;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
public class WpsFileSaveResponse extends WpsResponse implements Serializable {

    public WpsFileSaveResponse() {
    }

    public WpsFileSaveResponse(WpsFileEntity file) {
        this.file = file;
    }

    private WpsFileEntity file;

    public WpsFileEntity getFile() {
        return file;
    }

    public void setFile(WpsFileEntity file) {
        this.file = file;
    }
}
