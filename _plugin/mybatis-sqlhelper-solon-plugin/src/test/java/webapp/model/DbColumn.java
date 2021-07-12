package webapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author JcYen
 * @Date 2020/5/27
 * @Version 1.0
 */
@Getter
@Setter
@ToString
public class DbColumn {

    /**
     * 是否主键
     */
    private Boolean isKey;

    /**
     * 是否为空
     */
    private Boolean isNull;

    /**
     * 是否创建列
     */
    private Boolean isCreate;

    /**
     * 是否更新列
     */
    private Boolean isUpdate;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 最大长度
     */
    private Long maxLength;

    /**
     * 备注
     */
    private String comment;

    /**
     * 整数位精确度
     */
    private Integer numericPrecision;

    /**
     * 小数位精确度
     */
    private Integer numericScale;

}
