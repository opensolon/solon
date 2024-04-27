增加 properties 格式的参数，及渲染输出：

### 参数效果

*  `?user.id=1&user.name=noear&user.aaa[]=1&user.aaa[]=2`
*  `?id=1&name=noear&aaa[]=1&aaa[]=2`

### 输出效果

```properties
aaa[0]=1
aaa[1]=2
date=Sat Apr 27 08:46:52 CST 2024
id=1
name=noear
sex=0
```