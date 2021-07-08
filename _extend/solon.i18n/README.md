

```yml
solon.i18n.message:
  base: message
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