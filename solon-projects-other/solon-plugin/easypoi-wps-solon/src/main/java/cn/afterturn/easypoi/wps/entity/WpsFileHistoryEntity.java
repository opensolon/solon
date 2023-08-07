package cn.afterturn.easypoi.wps.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsFileHistoryEntity  implements Serializable {

    /**
     * 文件id,字符串长度小于40
     */
    private String id;
    /**
     * 文件名
     */
    private String name;
    /**
     * 当前版本号，位数小于11
     */
    private int    version = 1;
    /**
     * 文件大小，单位为kb
     */
    private int    size;
    /**
     * 创建时间，时间戳，单位为秒
     */
    private long   create_time;
    /**
     * 修改时间，时间戳，单位为秒
     */
    private long   modify_time;

    private WpsUserEntity creator;
    private WpsUserEntity modifier;
}
