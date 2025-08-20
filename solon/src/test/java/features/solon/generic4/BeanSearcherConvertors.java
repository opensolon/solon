package features.solon.generic4;

import features.solon.generic4.converter.*;
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanSearcherConvertors {

    @Managed
    @Condition(onMissingBean = BoolParamConvertor.class)
    public BoolParamConvertor boolParamConvertor() {
        return new BoolParamConvertor();
    }

    @Managed
    @Condition(onMissingBean = NumberParamConvertor.class)
    public NumberParamConvertor numberParamConvertor() {
        return new NumberParamConvertor();
    }

    @Managed
    @Condition(onMissingBean = DateParamConvertor.class)
    public DateParamConvertor dateParamConvertor() {
        return new DateParamConvertor();
    }

    @Managed
    @Condition(onMissingBean = TimeParamConvertor.class)
    public TimeParamConvertor timeParamConvertor() {
        return new TimeParamConvertor();
    }

    @Managed
    @Condition(onMissingBean = DateTimeParamConvertor.class)
    public DateTimeParamConvertor dateTimeParamConvertor() {
        return new DateTimeParamConvertor();
    }

    @Managed
    @Condition(onMissingBean = EnumParamConvertor.class)
    public EnumParamConvertor enumParamConvertor() {
        return new EnumParamConvertor();
    }

    // FieldConvertor

    @Managed
    @Condition(onMissingBean = NumberFieldConvertor.class)
    public NumberFieldConvertor numberFieldConvertor() {
        return new NumberFieldConvertor();
    }


    @Managed
    @Condition(onMissingBean = BoolNumFieldConvertor.class)
    public BoolNumFieldConvertor boolNumFieldConvertor() {
        return new BoolNumFieldConvertor();
    }

    @Managed
    @Condition(onMissingBean = BoolFieldConvertor.class)
    public BoolFieldConvertor boolFieldConvertor() {
        return new BoolFieldConvertor();
    }

    @Managed
    @Condition(onMissingBean = DateFieldConvertor.class)
    public DateFieldConvertor dateFieldConvertor() {
        return new DateFieldConvertor();
    }

    @Managed
    @Condition(onMissingBean = TimeFieldConvertor.class)
    public TimeFieldConvertor timeFieldConvertor() {
        return new TimeFieldConvertor();
    }

    @Managed
    @Condition(onMissingBean = EnumFieldConvertor.class)
    public EnumFieldConvertor enumFieldConvertor() {
        return new EnumFieldConvertor();
    }

    // 在 springboot 那边，是用单独类处理的；在 solon 这边，用函数
    @Managed
    @Condition(onMissingBean = JsonFieldConvertor.class)
    public JsonFieldConvertor jsonFieldConvertor() {
        return new JsonFieldConvertor();
    }

    @Managed
    @Condition(onMissingBean = OracleTimestampFieldConvertor.class)
    public OracleTimestampFieldConvertor oracleTimestampFieldConvertor() {
        return new OracleTimestampFieldConvertor();
    }

    @Managed
    @Condition(onMissingBean = EnumLabelLoader.class)
    public EnumLabelLoader enumLabelLoader(){
        return new EnumLabelLoader();
    }

    /// ////////////////////

    @Managed
    @Condition(onMissingBean = BeanReflector.class)
    public BeanReflector beanReflector(List<FieldConvertor.BFieldConvertor> convertors) {
        return new BeanReflectorImpl(convertors);
    }

    @Managed
    @Condition(onMissingBean = ResultFilter.class)
    public ResultFilter labelResultFilter(List<LabelLoader<?>> labelLoaders) {
        return new ResultFilter(labelLoaders);
    }
}
