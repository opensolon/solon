package cn.afterturn.easypoi.wps.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsUserAclEntity  implements Serializable {
    /**
     * 重命名权限，1为打开该权限，0为关闭该权限，默认为0
     */
    private Integer rename  = 0;
    /**
     * 历史版本权限，1为打开该权限，0为关闭该权限,默认为1
     */
    private Integer history = 0;
    /**
     * 复制
     */
    private Integer copy    = 1;
    /**
     * 导出PDF
     */
    private Integer export  = 1;
    private Integer print   = 1;
}
