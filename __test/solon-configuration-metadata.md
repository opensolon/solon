
# 关于 solon-configuration-metadata.json 的草案


### 方案1，采用 spring 方案的简化版，但不与 java 类映射

分两个大属性：properties，hints


| 属性               | 类型      | 说明  |
|------------------|---------|-----|
| properties       | []      | 属性  |
| - name           | string  | 名字  |
| - type           | string  | 类型  |
| - defaultValue   | object  | 默认值 |
| - description    | string  | 描述  |
|                  |         |     |
| hints            | []      | 提示  |
| - name           | string  | 名字  |
| - values         | []      | 可选值 |
| - - value        | object    | 值   |
| - - description  | string  | 描述  |

例：

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



### 方案2，在方案1基础上，两大属性合体

只有一大属性：properties

| 属性               | 类型      | 说明  |
|------------------|---------|-----|
| properties       | []      | 属性  |
| - name           | string  | 名字  |
| - type           | string  | 类型  |
| - defaultValue   | object  | 默认值 |
| - description    | string  | 描述  |
| - values         | []      | 可选值 |
| - - value        | object    | 值   |
| - - description  | string  | 描述  |

例：

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
      "description": "驱动类型",
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

### 方案3，在方案2基础上，取消 properties 属性，增加分组（只是在编辑维护时，做个隔开）


| 属性              | 类型      | 说明  |
|-----------------|---------|-----|
| virtual group   | []      | 属性  |
| - name          | string  | 名字  |
| - type          | string  | 类型  |
| - defaultValue  | object  | 默认值 |
| - description   | string  | 描述  |
| - values        | []      | 可选值 |
| - - value       | object    | 值   |
| - - description | string  | 描述  |

例：

```json
{
  "server": [
    {
      "name": "server.port",
      "type": "java.lang.Integer",
      "defaultValue": "8080",
      "description": "服务端口"
    }
  ],
  "cache": [
    {
      "name": "cache.driverType",
      "type": "java.lang.String",
      "defaultValue": "local",
      "description": "驱动类型",
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