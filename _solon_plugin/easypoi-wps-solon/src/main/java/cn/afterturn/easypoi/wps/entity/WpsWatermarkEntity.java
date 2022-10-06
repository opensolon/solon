package cn.afterturn.easypoi.wps.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 水印配置
 *
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsWatermarkEntity implements Serializable {
    /**
     * 水印类型， 0为无水印； 1为文字水印
     */
    private Integer type = 0;
    /**
     * 文字水印的文字，当type为1时此字段必选
     */
    private Integer value;
    /**
     * 水印的透明度，非必选，有默认值
     * "rgba( 192, 192, 192, 0.6 )"
     */
    private Integer fillstyle;
    /**
     * 水印的字体，非必选，有默认值
     * "bold 20px Serif"
     */
    private Integer font;
    /**
     * 水印的旋转度，非必选，有默认值
     * -0.7853982
     */
    private String  rotate;
    /**
     * 水印水平间距，非必选，有默认值
     */
    private Integer horizontal;
    /**
     * 水印垂直间距，非必选，有默认值
     */
    private Integer vertical;
}
