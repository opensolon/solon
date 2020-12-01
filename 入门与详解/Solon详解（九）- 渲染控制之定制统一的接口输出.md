> 下面这个场景是特意为此文设计出来的。万一有类似的出现了。。。Spring mini -Solon 可以给你一个so easy的支持。

Solon 特性之一：

可让控制器实现 Render，从而接管控制器的渲染动作。



### 一、定义个接口基类，并实现渲染接口

**渲染逻辑如下：**

1. 如果对象是null，跳过不管
2. 如果是String，直接输出
3. 如果是ONode，做为Json输出
4. 如果是UapiCode，将其转为Result，再序列化为Json输出
5. 如果是Throwable，将其转为Result，再序列化为Json输出
6. 如果是其它数据，直接序列化为Json输出

**代码：**

```java
//这个注解可继承，用于支持子类的验证
//
@Valid
public class UapiBase implements Render {
    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (obj == null) {
            return;
        }

        if (obj instanceof String) {
            ctx.output((String) obj);
        } else {
            if (obj instanceof ONode) {
                ctx.outputAsJson(((ONode) obj).toJson());
            } else {
                if (obj instanceof UapiCode) {
                    //此处是重点，把一些特别的类型进行标准化转换
                    //
                    UapiCode err = (UapiCode) obj;
                    obj = XResult.failure(err.getCode(), UapiCodes.getDescription(err));
                }

                if (obj instanceof Throwable) {
                    //此处是重点，把异常进行标准化转换
                    //
                    Throwable err = (Throwable) obj;
                    obj = XResult.failure(err.getMessage());
                }

                ctx.outputAsJson(ONode.stringify(obj));
            }
        }
    }
}
```

### 二、接口示例


#### 1. 白名单接口

此接口做个白名单检测。如果成功，则返加符串：OK

```java
@Controller
public class CMD_run_whitelist_check extends UapiBase {
    //此处的@NotEmpty验证，如果没通过会抛出UapiCode
    @NotEmpty({"type", "value"})
    @Mapping("/run/whitelist/check/")
    public String cmd_exec(Context ctx, String type, String value) throws Exception {
        String tags = ctx.param("tags", "");

        if (tags.contains("client")) {
            if (DbWaterCfgApi.whitelistIgnoreClient()) {
                return "OK";
            }
        }

        if (DbWaterCfgApi.isWhitelist(tags, type, value)) {
            return ("OK");
        } else {
            return (value + ",not is whitelist!");
        }
    }
}
```

#### 2. 通知推送接口

此接口只能白名单里的IP方可调用。执行后返回：Result

```java
//此处的@Whitelist验证，如果没通过会抛出UapiCode
@Whitelist
@Controller
public class CMD_run_push extends UapiBase {
    //此处的@NotEmpty验证，如果没通过会抛出UapiCode
    @NotEmpty({"msg", "target"})
    @Mapping("/run/push/")
    public Result cmd_exec(String msg, String target) throws Exception {

        List<String> list = new ArrayList<String>();
        for (String str : target.split(",")) {
            if (str.equals("@alarm")) {
                List<String> mobiles = DbWaterCfgApi.getAlarmMobiles();

                list.addAll(mobiles);
            } else {
                list.add(str);
            }
        }

        String rest = ProtocolHub.heihei.push(Config.water_service_name, list, msg);

        if (TextUtils.isEmpty(rest) == false) {
            return Result.succeed(ONode.load(rest));
        } else {
            return Result.failure();
        }
    }
}
```

#### 3. 配置获取接口

此接口返回一组配置，以ONode类型返回

```java
//@Logging是个拦截器，会对请求输入进行记录
@Logging
//此处的@Whitelist验证，如果没通过会抛出UapiCode
@Whitelist
@Controller
public class CMD_cfg_get extends UapiBase {
    //此处的@NotEmpty验证，如果没通过会抛出UapiCode
    @NotEmpty("tag")
    @Mapping("/cfg/get/")
    public ONode cmd_exec(Context ctx, String tag) throws Throwable {
        ONode nList = new ONode().asObject();

        if (TextUtils.isEmpty(tag) == false) {
            List<ConfigModel> list = DbWaterCfgApi.getConfigByTag(tag);

            Date def_time = new Date();

            for (ConfigModel m1 : list) {
                if (m1.update_fulltime == null) {
                    m1.update_fulltime = def_time;
                }

                ONode n = nList.getNew(m1.key);
                n.set("key", m1.key);
                n.set("value", m1.value);

                if (m1.update_fulltime == null) {
                    n.set("lastModified", 0);
                } else {
                    n.set("lastModified", m1.update_fulltime.getTime());
                }
            }
        }

        return nList;
    }
}
```

> 此文的渲染控制重点是对抛出来的UapiCode和Throwable，进行有效的控制并以统一的Result形态输出。对外接口开发时，还是效果可期的。当然，也可以用此特性干点别的什么事儿。



### 附：项目地址

* gitee:  [https://gitee.com/noear/solon](https://gitee.com/noear/solon)
* github:  [https://github.com/noear/solon](https://github.com/noear/solon)



