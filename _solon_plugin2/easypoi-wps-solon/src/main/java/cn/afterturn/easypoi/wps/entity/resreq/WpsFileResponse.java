package cn.afterturn.easypoi.wps.entity.resreq;

import cn.afterturn.easypoi.wps.entity.WpsFileEntity;
import cn.afterturn.easypoi.wps.entity.WpsUserEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取文件返回对象
 *
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsFileResponse extends WpsResponse implements Serializable {

    private WpsFileEntity file;

    private WpsUserEntity user = new WpsUserEntity();
}
