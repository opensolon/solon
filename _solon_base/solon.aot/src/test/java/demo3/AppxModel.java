package demo3;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author noear 2023/2/24 created
 */

@TableName("appx")
public class AppxModel {
    private Integer agroupId;
    private String note;
    private String appKey;
    @TableId("app_id")
    private Integer appId;
    private Integer arIsExamine;

    public Integer getAgroupId() {
        return agroupId;
    }

    public Integer getAppId() {
        return appId;
    }

    public Integer getArIsExamine() {
        return arIsExamine;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getNote() {
        return note;
    }
}
