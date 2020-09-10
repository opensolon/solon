Solon 中也有一种非常解耦的扩展机制：Solon Plugin。这种扩展机制和Spring Factories很像，和SPI也很像。

### 一、Solon 中的扩展机制

在Solon的扩展插件加载机制，是在 `META-INF/solon/{packname}.properties` 文件中配置XPlugin的实现类名称和优先级别，然后在程序中读取这些配置文件并实例化。这种自定义的SPI机制是Solon Plugin扩展实现的基础。

具体在扩展项目添加申明如下：

* 添加配置：`src/main/resources/META-INF/solon/{packname}.properties`
  * 使用包做为文件名，是为了便于识别，且可避免冲突
* 配置内容：

```properties
solon.plugin={XPlugin impl}  #插件实现类
solon.plugin.priority=9      #加载优先级，越大越优先；默认不用配置
```

XPlugin的作用：

在应用启动过程中，在特定的序顺位置，获取运行权限；进而进行框架扩展。

### 二、扩展示例，插件：solon.extend.aspect 

这个插件，是为Solon提供 `@XDao` 和 `@XService` 扩展注解，进而实现class的动态代理能力；基于ASM实现，但算是比较克制，暂时没加别的功能。本例完整的项目源码：[https://gitee.com/noear/solon/tree/master/_extend/solon.extend.aspect](https://gitee.com/noear/solon/tree/master/_extend/solon.extend.aspect)，此处主要展示与扩展机制有关系的代码和配置。

* 代码文件：`src/main/java/org.noear.solon.extend.aspect.XPluginImp.java`，实现XPlugin接口：

```java
package org.noear.solon.extend.aspect;

import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;
import org.noear.solon.extend.aspect.annotation.XDao;
import org.noear.solon.extend.aspect.annotation.XService;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        //向Aop工厂注册Bean生成器；代理XDao注解的处理
        //
        Aop.factory().beanCreatorAdd(XDao.class, (clz, bw, anno) -> {
            //为BeanWrap设置class代理
            bw.proxySet(BeanProxyImp.global());
        });

        //向Aop工厂注册Bean生成器；代理XService注解的处理
        //
        Aop.factory().beanCreatorAdd(XService.class, (clz, bw, anno) -> {
            //为BeanWrap设置class代理
            bw.proxySet(BeanProxyImp.global());
        });
    }
}
```

* 配置文件：`src/main/resources/META-INF/solon/solon.extend.aspect.properties`，实现自申明效果：

```properties
solon.plugin=org.noear.solon.extend.aspect.XPluginImp
```

主框架会通过扫描 `META-INF/solon/` 文件夹下的所有 .properties 文件，进而发现各种扩展插件的XPlugin实现类。


* 应用示例

```java
@XService
public class AppService {
    @XInject
    SqlMapper sqlMapper1;

    //
    // @XService 注解，可为 bean 添加 class 动态代理；进而支持事务注解：@XTran
    //
    @XTran
    public void addApp(){
        sqlMapper1.appx_add();
    }
}
```

### 三、附：Solon应用启动顺序

1. 实例化 XApp.global()
2. 加载应用属性配置
3. 加载扩展文件夹
4. 扫描插件并排序记录（插件也可叫扩展组件）
5. 运行builder函数（如果它不为null）
6. 运行插件
7. 扫描source目录并加载java bean
8. 加载渲染关系
9. 完成

