

```
//默认国际化消息配置（此为约定，不能改）
resources/i18n/message.properties
resources/i18n/message_en.properties
resources/i18n/message_zh_CN.properties


```

beetl::
```html
<div>
    i18n::${i18n["login.title"]}
    i18n::${@i18n.get("login.title")}
    i18n::${@i18n.getAndFormat("login.title",12,"a")}
</div>
```

enjoy::
```html
<div>
    i18n::#(i18n.get("login.title"))
    i18n::#(i18n.getAndFormat("login.title",12,"a"))
</div>
```

freemarker::
```html
<div>
    i18n::${i18n["login.title"]}
    i18n::${i18n.get("login.title")}
    i18n::${i18n.getAndFormat("login.title",12,"a")}
</div>
```

thymeleaf::
```html
<div>
    i18n::<span th:text='${i18n.get("login.title")}'></span>
    i18n::<span th:text='${i18n.getAndFormat("login.title",12,"a")}'></span>
</div>
```

velocity::
```html
<div>
    i18n::${i18n["login.title"]}
    i18n::${i18n.get("login.title")}
    i18n::${i18n.getAndFormat("login.title",12,"a")}
</div>
```