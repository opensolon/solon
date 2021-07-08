

```yml
solon.i18n.message:
  base: message
```



beetl::
```html
<div>
i18n::${i18n["login.title"]}
</div>
```

enjoy::
```html
<div>
i18n::#(i18n["login.title"])
</div>
```

freemarker::
```html
<div>
i18n::${i18n["login.title"]}
</div>
```

thymeleaf::
```html
<div>
i18n::<span th:text='${i18n["login.title"]}'></span>
</div>
```

velocity::
```html
<div>
i18n::${i18n["login.title"]}
</div>
```