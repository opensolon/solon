package micrometer;
import java.io.Serializable;


/**
 * 计项目
 *
 * @author bai
 * @date 2023/07/27
 */
public class MeterItem implements Serializable {

    /**
     * 统计
     */
    String statistic;

    /**
     * 价值
     */
    Object value;

    /**
     * 构造
     *
     * @param statistic 统计
     * @param value     价值
     */
    public MeterItem(String statistic, Object value) {
        this.statistic = statistic;
        this.value = value;
    }

    /**
     * 得到统计量
     *
     * @return {@link String}
     */
    public String getStatistic() {
        return statistic;
    }

    /**
     * 设置统计
     *
     * @param statistic 统计
     */
    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    /**
     * 获得值
     *
     * @return {@link Object}
     */
    public Object getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 价值
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
