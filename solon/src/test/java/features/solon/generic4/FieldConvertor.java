package features.solon.generic4;


public interface FieldConvertor {

    boolean supports(FieldMeta meta, Class<?> valueType);


    default Object convert(FieldMeta meta, Object value) {
        return value;
    }


    interface BFieldConvertor extends FieldConvertor { }


    interface MFieldConvertor extends FieldConvertor { }


    interface ParamConvertor extends FieldConvertor { }

}