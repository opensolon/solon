
**分三种行为**

* 生成（默认进行注册）
* 构建（每次创建新包装，成功后才注册）
* 注入

```swift
//
//旧的wrap()相当于wrapAndPut()，操作时无法选择
//
+wrap(clz, bean)->BeanWrap           //包装并不注册
+wrapAndPut(clz)->BeanWrap           //包装并注册
+wrapAndPut(clz, bean)->BeanWrap     //包装并注册

+get(key:String) -> T       //获取有name的bean
+get(clz)        -> T       //获取只有类型的bean


+getAsyn(key:String,callback)   //异步获取有name的bean
+getAsyn(clz,callback)          //异步获取只有类型的bean

+inject(obj)                    //注入
+inject(obj, popS:Properties)   //注入配置
```