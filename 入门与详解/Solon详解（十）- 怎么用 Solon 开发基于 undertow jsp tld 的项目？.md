`Solon` 开发 `jsp` 项目是非常简单的，只要改用 `jetty` 启动器 或者 `undertow` 启动器，其它也没特别之处了。此文用 `undertow + jsp + tld` 这个套路搞一把：

#### 一、 开始Meven配置走起

> 用solon 做 undertow + jsp 的开发；只需要配置一下 meven 即可（不需要其它的额外处理或启用）

```xml
    <parent>
        <groupId>org.noear</groupId>
        <artifactId>solon-parent</artifactId>
        <version>1.1</version>
    </parent>
    
    <dependencies>
        <!-- 添加 solon web 开发包 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon-web</artifactId>
            <type>pom</type>
            <exclusions>
                <!-- 排除默认的 jlhttp 启动器 -->
                <exclusion>
                    <groupId>org.noear</groupId>
                    <artifactId>solon.boot.jlhttp</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- 添加 undertow 启动器 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon.boot.undertow</artifactId>
        </dependency>

        <!-- 添加 undertow jsp 扩展支持包 -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon.extend.undertow.jsp</artifactId>
        </dependency>

        <!-- 添加 jsp 视图渲染器（可以添加一堆别的view插件） -->
        <dependency>
            <groupId>org.noear</groupId>
            <artifactId>solon.view.jsp</artifactId>
        </dependency>


        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.14.4</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

#### 二、 其它代码和平常开发就差不多了

```
//资源路径说明（不用配置）
resources/application.properties（或 application.yml） 为应用配置文件
resources/static/ 为静态文件根目标
resources/WEB-INF/view/ 为视图文件根目标（支持多视图共存）

//调试模式：
启动参数添加：-deubg=1
```

* 添加个控制器 `src/main/java/webapp/controller/HelloworldController.java`

```java
@XController
public class HelloworldController {

    //这里注入个配置
    @XInject("${custom.user}")
    protected String user;

    @XMapping("/helloworld")
    public ModelAndView helloworld(XContext ctx){
        UserModel m = new UserModel();
        m.setId(10);
        m.setName("刘之西东");
        m.setSex(1);

        ModelAndView vm = new ModelAndView("helloworld.jsp"); //如果是ftl模板，把后缀改为：.ftl 即可

        vm.put("title","demo");
        vm.put("message","hello world!");

        vm.put("m",m);

        vm.put("user", user);

        vm.put("ctx",ctx);

        return vm;
    }
}
```

* 再搞个自定义标签 `src/main/java/webapp/widget/FooterTag.java` （对jsp来说，这个演示很重要）

```java
public class FooterTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        try {
            String path = XContext.current().path();

            //当前视图path
            StringBuffer sb = new StringBuffer();
            sb.append("<footer>");
            sb.append("我是自定义标签，FooterTag；当前path=").append(path);
            sb.append("</footer>");
            pageContext.getOut().write(sb.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }
}
```

* 加tld描述文件 `src/main/resources/WEB-INF/tags.tld` （位置别乱改，就放这儿...）

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<taglib xmlns="http://java.sun.com/xml/ns/j2ee"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd" version="2.0">

    <description>自定义标签库</description>
    <tlib-version>1.1</tlib-version>
    <short-name>ct</short-name>
    <uri>/tags</uri>

    <tag>
        <name>footer</name>
        <tag-class>webapp.widget.FooterTag</tag-class>
        <body-content>empty</body-content>
    </tag>

</taglib>
```

* 视图 `src/main/resources/WEB-INF/view/helloworld.jsp`

```html
<%@ page import="java.util.Random" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ct" uri="/tags" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
<div>
    context path: ${ctx.path()}
</div>
<div>
    properties: custom.user :${user}
</div>
<main>
    ${m.name} : ${message} （我想<a href="/jinjin.htm">静静</a>）
</main>
<ct:footer/>
</body>
</html>
```

#### 三、 疑问

一路上没有web.xml ? 是的，没有。

#### 四、 源码

[源码：demo05.solon_mvc_undertow_jsp](https://gitee.com/noear/solon_demo/tree/master/demo05.solon_mvc_undertow_jsp)
