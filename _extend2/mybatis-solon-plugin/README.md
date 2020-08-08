
```yaml
mybatis.db1f:
    typeAliases:
        - "webapp.model"    #支持包名
    mappers:
        - "webapp.dso.db1"  #支持包名，或xml

mybatis.db2f:
    typeAliases:
        - "webapp.model"
    mappers:
        - "webapp.dso.db2" #支持包名，或xml
```