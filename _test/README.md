# Solon 单元测试项目

大概有几百个功能测试用例。


在 jdk9 之后，如果出现序列化权限问题。可添加jvm参数：
--add-opens java.base/java.lang=ALL-UNNAMED

或

--illegal-access=permit

示例：
java --add-opens java.base/java.lang=ALL-UNNAMED -jar xxx.jar