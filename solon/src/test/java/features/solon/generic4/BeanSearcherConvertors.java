package features.solon.generic4;

import features.solon.generic4.converter.*;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanSearcherConvertors {

    @Bean
    @Condition(onMissingBean = BoolParamConvertor.class)
    public BoolParamConvertor boolParamConvertor() {
        return new BoolParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = NumberParamConvertor.class)
    public NumberParamConvertor numberParamConvertor() {
        return new NumberParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = DateParamConvertor.class)
    public DateParamConvertor dateParamConvertor() {
        return new DateParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = TimeParamConvertor.class)
    public TimeParamConvertor timeParamConvertor() {
        return new TimeParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = DateTimeParamConvertor.class)
    public DateTimeParamConvertor dateTimeParamConvertor() {
        return new DateTimeParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = EnumParamConvertor.class)
    public EnumParamConvertor enumParamConvertor() {
        return new EnumParamConvertor();
    }

    // FieldConvertor

    @Bean
    @Condition(onMissingBean = NumberFieldConvertor.class)
    public NumberFieldConvertor numberFieldConvertor() {
        return new NumberFieldConvertor();
    }


    @Bean
    @Condition(onMissingBean = BoolNumFieldConvertor.class)
    public BoolNumFieldConvertor boolNumFieldConvertor() {
        return new BoolNumFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = BoolFieldConvertor.class)
    public BoolFieldConvertor boolFieldConvertor() {
        return new BoolFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = DateFieldConvertor.class)
    public DateFieldConvertor dateFieldConvertor() {
        return new DateFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = TimeFieldConvertor.class)
    public TimeFieldConvertor timeFieldConvertor() {
        return new TimeFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = EnumFieldConvertor.class)
    public EnumFieldConvertor enumFieldConvertor() {
        return new EnumFieldConvertor();
    }

    // 在 springboot 那边，是用单独类处理的；在 solon 这边，用函数
    @Bean
    @Condition(onMissingBean = JsonFieldConvertor.class)
    public JsonFieldConvertor jsonFieldConvertor() {
        return new JsonFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = OracleTimestampFieldConvertor.class)
    public OracleTimestampFieldConvertor oracleTimestampFieldConvertor() {
        return new OracleTimestampFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = EnumLabelLoader.class)
    public EnumLabelLoader enumLabelLoader(){
        return new EnumLabelLoader();
    }

    /// ////////////////////

    @Bean
    @Condition(onMissingBean = BeanReflector.class)
    public BeanReflector beanReflector(List<FieldConvertor.BFieldConvertor> convertors) {
        return new BeanReflectorImpl(convertors);
    }

    @Bean
    @Condition(onMissingBean = ResultFilter.class)
    public ResultFilter labelResultFilter(List<LabelLoader<?>> labelLoaders) {
        return new ResultFilter(labelLoaders);
    }
}
