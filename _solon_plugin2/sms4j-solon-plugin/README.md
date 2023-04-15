
# sms4j-solon-plugin

## 1、sms4j用法

[sms4j - 官网]([SMS4J | SMS4J (wind.kim)](https://wind.kim/))

- 1）在`pom.xml`中引入依赖

  ```xml
  <dependency>
	    <groupId>org.noear</groupId>
	    <artifactId>sms4j-solon-plugin</artifactId>
  </dependency>
  ```
  
- 2）配置文件和官网的一样 sms4j  [🛠️进阶配置 ](https://wind.kim/doc/start/jinjiepeizhi.html) 参考如下：

  ```properties
   sms:
      alibaba:
        #阿里云的accessKey
        accessKeyId: 您的accessKey
        #阿里云的accessKeySecret
        accessKeySecret: 您的accessKeySecret
        #短信签名
        signature: 测试签名
        #模板ID 用于发送固定模板短信使用
        templateId: SMS_215125134
        #模板变量 上述模板的变量
        templateName: code
        #请求地址 默认为dysmsapi.aliyuncs.com 如无特殊改变可以不用设置
        requestUrl: dysmsapi.aliyuncs.com
      huawei:
        #华为短信appKey
        appKey: 5N6fvXXXX920HaWhVXXXXXX7fYa
        #华为短信appSecret
        app-secret: Wujt7EYzZTBXXXXXXEhSP6XXXX
        #短信签名
        signature: 华为短信测试
        #通道号
        sender: 8823040504797
        #模板ID 如果使用自定义模板发送方法可不设定
        template-id: acXXXXXXXXc274b2a8263479b954c1ab5
        #华为回调地址，如不需要可不设置或为空
        statusCallBack:
         #华为分配的app请求地址
        url: https://XXXXX.cn-north-4.XXXXXXXX.com:443
  ```
  
- 3）参考官网

  ```java
          //阿里云向此手机号发送短信
          SmsFactory.createSmsBlend(SupplierType.ALIBABA).sendMessage("18888888888","123456");
          //华为短信向此手机号发送短信
          SmsFactory.createSmsBlend(SupplierType.HUAWEI).sendMessage("16666666666","000000");
  ```