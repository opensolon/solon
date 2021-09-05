
Demo
```
https://gitee.com/noear/captcha/tree/master/service/solon
https://gitee.com/noear/captcha/tree/master/view/html
```



参数： https://captcha.anji-plus.com/#/doc

配置示例

```properties

# 滑动验证，底图路径，不配置将使用默认图片
# 支持全路径
# 支持项目路径,以classpath:开头,取resource目录下路径,例：classpath:images/jigsaw
aj.captcha.jigsaw=classpath:images/jigsaw
    #滑动验证，底图路径，不配置将使用默认图片
    ##支持全路径
# 支持项目路径,以classpath:开头,取resource目录下路径,例：classpath:images/pic-click
aj.captcha.pic-click=classpath:images/pic-click

# 对于分布式部署的应用，我们建议应用自己实现CaptchaCacheService，比如用Redis或者memcache，
# 参考CaptchaCacheServiceRedisImpl.java
# 如果应用是单点的，也没有使用redis，那默认使用内存。
# 内存缓存只适合单节点部署的应用，否则验证码生产与验证在节点之间信息不同步，导致失败。
# ！！！ 注意啦，如果应用有使用spring-boot-starter-data-redis，
# 请打开CaptchaCacheServiceRedisImpl.java注释。
# redis ----->  SPI： 在resources目录新建META-INF.services文件夹(两层)，参考当前服务resources。
# 缓存local/redis...
aj.captcha.cache-type=local
# local缓存的阈值,达到这个值，清除缓存
#aj.captcha.cache-number=1000
# local定时清除过期缓存(单位秒),设置为0代表不执行
#aj.captcha.timing-clear=180
#spring.redis.host=10.108.11.46
#spring.redis.port=6379
#spring.redis.password=
#spring.redis.database=2
#spring.redis.timeout=6000

# 验证码类型default两种都实例化。
aj.captcha.type=default
# 汉字统一使用Unicode,保证程序通过@value读取到是中文，可通过这个在线转换;yml格式不需要转换
# https://tool.chinaz.com/tools/unicode.aspx 中文转Unicode
# 右下角水印文字(我的水印)
aj.captcha.water-mark=\u6211\u7684\u6c34\u5370
# 右下角水印字体(不配置时，默认使用文泉驿正黑)
# 由于宋体等涉及到版权，我们jar中内置了开源字体【文泉驿正黑】
# 方式一：直接配置OS层的现有的字体名称，比如：宋体
# 方式二：自定义特定字体，请将字体放到工程resources下fonts文件夹，支持ttf\ttc\otf字体
# aj.captcha.water-font=WenQuanZhengHei.ttf
# 点选文字验证码的文字字体(文泉驿正黑)
# aj.captcha.font-type=WenQuanZhengHei.ttf
# 校验滑动拼图允许误差偏移量(默认5像素)
aj.captcha.slip-offset=5
# aes加密坐标开启或者禁用(true|false)
aj.captcha.aes-status=true
# 滑动干扰项(0/1/2)
aj.captcha.interference-options=2

aj.captcha.history-data-clear-enable=false

# 接口请求次数一分钟限制是否开启 true|false
aj.captcha.req-frequency-limit-enable=false
# 验证失败5次，get接口锁定
aj.captcha.req-get-lock-limit=5
# 验证失败后，锁定时间间隔,s
aj.captcha.req-get-lock-seconds=360
# get接口一分钟内请求数限制
aj.captcha.req-get-minute-limit=30
# check接口一分钟内请求数限制
aj.captcha.req-check-minute-limit=60
# verify接口一分钟内请求数限制
aj.captcha.req-verify-minute-limit=60

```

后端较验示例：

```java

@Controller
public class LoginController {
    @Inject
    private CaptchaService captchaService;

    @Post
    @Mapping("/login")
    public ResponseModel get(CaptchaVO captchaVO) {
        //必传参数：captchaVO.captchaVerification
        ResponseModel response = captchaService.verification(captchaVO);
        if (response.isSuccess() == false) {
            //验证码校验失败，返回信息告诉前端
            //repCode  0000  无异常，代表成功
            //repCode  9999  服务器内部异常
            //repCode  0011  参数不能为空
            //repCode  6110  验证码已失效，请重新获取
            //repCode  6111  验证失败
            //repCode  6112  获取验证码失败,请联系管理员
            //repCode  6113  底图未初始化成功，请检查路径
            //repCode  6201  get接口请求次数超限，请稍后再试!
            //repCode  6206  无效请求，请重新获取验证码
            //repCode  6202  接口验证失败数过多，请稍后再试
            //repCode  6204  check接口请求次数超限，请稍后再试!
        }
        return response;
    }
}
```