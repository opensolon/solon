### （一）定义个接口基类，并实现泻染接口

```java
@XValid
public class UapiBase implements XRender {
    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        if (obj == null) {
            return;
        }

        if (obj instanceof String) {
            ctx.outputAsJson((String) obj);
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

### （二）接口示例

如果控制器，实现过XRender。而将所有的渲染动作转发到其实现上。

#### 1. 白名单接口

```java
@Whitelist
@XController
public class CMD_run_whitelist_check extends UapiBase {
    @NotEmpty({"type", "value"})
    @XMapping("/run/whitelist/check/")
    public String cmd_exec(XContext ctx, String type, String value) throws Exception {
        String tags = ctx.param("tags", "");

        if (tags.contains("client")) {
            if (DbWaterCfgApi.whitelistIgnoreClient()) {
                return "OK";
            }
        }

        boolean isOk = DbWaterCfgApi.isWhitelist(tags, type, value);

        if (isOk) {
            return ("OK");
        } else {
            return (value + ",not is whitelist!");
        }
    }
}
```

#### 2. 通知推送接口

```java
@Whitelist
@XController
public class CMD_run_push extends UapiBase {

    @NotEmpty({"msg", "target"})
    @XMapping("/run/push/")
    public XResult cmd_exec(String msg, String target) throws Exception {

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
            return XResult.succeed(ONode.load(rest));
        } else {
            return XResult.failure();
        }
    }
}
```
