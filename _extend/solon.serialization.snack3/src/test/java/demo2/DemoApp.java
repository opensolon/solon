package demo2;

import org.noear.solon.Solon;
import org.noear.solon.serialization.JsonLongConverter;
import org.noear.solon.serialization.JsonStringConverter;
import org.noear.solon.serialization.snack3.SnackRenderFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author noear 2021/10/12 created
 */
public class DemoApp {
    public static void main(String[] args){
        Solon.start(DemoApp.class, args, app->{
            initMvcJsonCustom();
        });
    }

    /**
     * 初始化json定制（需要在插件运行前定制）
     * */
    private static void initMvcJsonCustom(){
        //通过转换器，做简单类型的定制
        SnackRenderFactory.global.addConvertor(Date.class,
                (JsonLongConverter<Date>) source -> source.getTime());

        SnackRenderFactory.global.addConvertor(LocalDate.class,
                (JsonStringConverter<LocalDate>) source -> source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));


        //通过编码器，做复杂类型的原生定制（基于框架原生接口）
        SnackRenderFactory.global.addEncoder(Date.class, (data, node) ->
                node.val().setNumber(data.getTime()));
    }
}
