## solon-gradle-plugin

#### 1.打包插件配置示例

`build.gradle`

```groovy
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        classpath 'org.noear:solon-gradle-plugin:x.y.z'
    }
}


// 使用
apply plugin: 'org.noear.solon'


compileJava {
    // 这两个配置可以不添加了，插件中会默认自动添加
    options.encoding = "UTF-8"
    options.compilerArgs << "-parameters"
}

// 配置启动文件名
solon {
    mainClass = "com.example.demo.App"
}

// 也可以针对 jar包和 war包指定不同的 mainClass

solonJar{
    mainClass = "com.example.demo.App"
}

// 使用 solonWar 需要添加 war 插件
solonWar{
    mainClass = "com.example.demo.App"
}

```



