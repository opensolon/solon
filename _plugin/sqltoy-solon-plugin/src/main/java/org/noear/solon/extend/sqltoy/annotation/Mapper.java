package org.noear.solon.extend.sqltoy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 用于Interface映射Xml文件
 * 说明：
 * 1.在@Sql中写 sqltoy Sql或者 sqlId
 * 2.没有@Sql注解时 通过方法名映射sqlId
 * 3.返回值分为Page,List,Entity和直接值(如：Integer等)，当其中Page,List的类型为直接值或返回值类型为直接值时,具体类型由sql中查询结果决定
 * 4.方法参数没有用@Param指定时，参数可有0～2个,两个参数时其中一个为Page,另外一个为Map或Entity,顺序不限。如方法中有用@Param指定，
 *   除Page类型外，其他未带@Param的参数失效。@Param可指定多个
 * 5.当为default方法时，直接调用default方法
 *&nbsp;@Mapper
 * public interface SomeMapper{
 *
 *  Page pageQuery(Page page);//分页，映射id=pageQuery的sql,未指定Page范型时，Page中的数据未HashMap
 *
 *  Page&lt;User> pageQuery1(Page page,Map params);//指定返回类型
 *
 *  List listMapByMapParams(User params);//根据参数查询
 *
 *  //通过@Param指定参数名,可用@Param("另一个名字")来重新为参数命名，当没有设置编译时保持参数名的情况下，可用这种方式明确参数名称
 *  List&lt;User> findByName(@Param String name，@Param Integer age,Page page);
 *
 *  Long searchCount(User user);
 *
 *&nbsp; @Sql("select * from t_user where #[user_name=:userName]")  //sql注解中使用sql时，无法使用translate等高级功能
 *  User findBySql(User params);
 *
 *&nbsp; @Sql("queryId")
 *  User findOther(User params);
 *
 *  default int someMethod(){//不做任何处理,直接调用
 *      return this.searchCount(new User());
 *  }
 * }
 * </pre>
 *
 * @author 夜の孤城
 * @since 1.5
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Mapper {
    String value() default "";
}
