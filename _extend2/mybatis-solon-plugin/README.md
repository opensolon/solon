
```yaml
mybatis.db1f:
    typeAliases:
        - "webapp.model"    #支持包名
    mappers:
        - "webapp.dso.db1"            #支持包名
        - "webapp/dso/db1/mapp.xml"   #支持mapper xml
        - "webapp/dso/db1/mapp.class" #支持mapper class (以 class 结尾)
```