
# 关于 solon-configuration-metadata.json

本方案借签 [string](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/configuration-metadata.html) 的方案，简化但完全兼容。以便复用一些工具。

### 1、约定

文件存放位置为插件包的：

```
resource/META-INF/solon/solon-configuration-metadata.json
```

采用 json 格式，分两个大属性：

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
| name         | string  | 属性的全名。名称使用小写句点分隔的形式（例, server.port）。此属性为必需。  |
| type         | string  | 属性的数据类型（例如，java.lang.String），或者完整的泛型类型（例, java.util.Map<java.lang.String, com.example.MyType>）。此属性为必需。  |
| defaultValue | object  | 默认值。非必需。 |
| description  | string  | 属性描述（简短、易懂）。非必需。  |

##### hints 字段说明


| 属性             | 类型      | 说明  |
|----------------|---------|-----|
| name           | string  | 属性的全名。此属性为必需。  |
| values       | []      | 用户可选择的值列表。 |
| - value      | object    | 值。   |
| - description | string  | 描述（简短、易懂）。  |



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
      "description": "缓存驱动类型"
    },
    {
      "name": "beetlsql.inters",
      "type": "java.lang.String[]",
      "description": "数据管理插件列表"
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