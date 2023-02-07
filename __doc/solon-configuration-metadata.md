
# 关于 solon-configuration-metadata.json

本方案借签 [string](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/configuration-metadata.html) 的方案，简化但完全兼容。以便复用一些工具。

### 1、方案

* 文件存放位置为插件包的：

resource/META-INF/solon/solon-configuration-metadata.json

* 总体格式分两个大属性：properties，hints。例：

```json
{
  "properties": [],
  "hints": []
}
```

### 2、具体字段说明

##### properties 字段说明

| 属性           | 类型      | 说明  |
|--------------|---------|-----|
| name         | string  | 名字  |
| type         | string  | 类型  |
| defaultValue | object  | 默认值 |
| description  | string  | 描述  |

##### hints 字段说明


| 属性             | 类型      | 说明  |
|----------------|---------|-----|
| name           | string  | 名字  |
| values       | []      | 可选值 |
| - value      | object    | 值   |
| - description | string  | 描述  |



### 3、完整格式示例：

```json
{
  "properties": [
    {
      "name": "server.port",
      "type": "java.lang.Integer",
      "defaultValue": "8080",
      "description": "服务端口"
    },
    {
      "name": "cache.driverType",
      "type": "java.lang.String",
      "defaultValue": "local",
      "description": "驱动类型"
    }
  ],
  "hints": [
    {
      "name": "cache.driverType",
      "values": [
        {
          "value": "local",
          "description": "本地缓存"
        },
        {
          "value": "redis",
          "description": "Redis缓存"
        },
        {
          "value": "memcached",
          "description": "Memcached缓存"
        }
      ]
    }
  ]
}
```

