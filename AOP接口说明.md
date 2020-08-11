
```swift
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