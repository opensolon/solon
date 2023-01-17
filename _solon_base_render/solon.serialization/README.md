

Json 序列化输出快捷配置：

```yaml
solon.serialization.json:
  dateAsFormat: 'yyyy-MM-dd HH:mm:ss' #配置日期格式（默认输出为时间戳）
  dateAsTimeZone: 'GMT+8' #配置时区
  dateAsTicks: false #将date转为毫秒数（和 dateAsFormat 二选一）
  longAsString: true #将long型转为字符串输出 （默认为false）
  boolAsInt: false   #将bool型转为字符串输出 （默认为false）
  nullStringAsEmpty: false
  nullBoolAsFalse: false
  nullNumberAsZero: false
  nullArrayAsEmpty: false
  nullAsWriteable: false #输出所有null值
```