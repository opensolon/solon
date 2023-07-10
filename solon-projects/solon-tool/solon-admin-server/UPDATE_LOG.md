
2023-07-10

* 取消 solon-web, solon.boot.undertow 依赖（有 solon-api 就可以了）
* 取消 okhttp 4.x 的依赖，改成 3.x （4.x 包加大了很多，没必要）
* 取消 gson 的依赖，添加 JsonUtils 工具类
* ServerProperties 的配置注入，可以注在"配置类"上
* Status 去掉通过注解指定输出值