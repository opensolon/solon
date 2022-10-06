package cn.afterturn.easypoi.wps.entity.resreq;

import cn.afterturn.easypoi.wps.entity.WpsUserEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsUserResponse extends WpsResponse implements Serializable {

    private List<WpsUserEntity> users = new ArrayList<>();
}
