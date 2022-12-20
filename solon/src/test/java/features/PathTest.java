package features;


import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2022/12/2 created
 */
@Configuration
public class PathTest {
    public static void main(String[] args){
        Solon.start(PathTest.class, args, app->{
            app.context().beanOnloaded(c->{
                c.subBeansOfType(String.class, bean->{
                    System.out.println(bean);
                });
            });
        });
    }

    @Bean("str1")
    public String str1(){
        return "1";
    }

    @Bean("str2")
    public String str2(){
        return "2";
    }
}
