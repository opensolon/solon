<h1 align="center" style="text-align:center;">
<img src="solon_icon.png" width="128" />
<br />
Solon v2.2.11-SNAPSHOT
</h1>
<p align="center">
	<strong>効率的なJavaアプリケーション開発フレームワーク、より小さく、より速く、より簡単に！</strong>
</p>
<p align="center">
	<a href="https://solon.noear.org/">https://solon.noear.org</a>
</p>

<p align="center">
    <a target="_blank" href="https://central.sonatype.com/search?q=org.noear%2520solon-parent">
        <img src="https://img.shields.io/maven-central/v/org.noear/solon.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:License-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk20-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-20-green.svg" alt="jdk-20" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/solon/stargazers'>
		<img src='https://gitee.com/noear/solon/badge/star.svg' alt='gitee star'/>
	</a>
    <a target="_blank" href='https://github.com/noear/solon/stargazers'>
		<img src="https://img.shields.io/github/stars/noear/solon.svg?logo=github" alt="github star"/>
	</a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-22200020-orange"/></a>
</p>

##### 言語： 日本語 | [中文](README.md) | [English](README_EN.md)

<hr />

起動が5～10倍速い、qpsは2～3倍高い、運転時のメモリ節約率は1/3～1/2、パッケージは1/2～1/10に縮小できます

<hr />

## 介绍：

**Solon** は効率的なJavaアプリケーション開発フレームワークであり、プラグインの豊富な開放的な生態でもあり、異なるプラグインを組み合わせて異なるニーズに対応する、カスタマイズが容易迅速な開発：

* **自制、簡潔、開放、生態**
* 支持 JDK8、JDK11、JDK17、JDK20
* Http、WebSocket、Socketの3つの信号統合の開発体験（通称：3ソース統合）
* 「注記」と「手動」の2種類のモードをサポートし、必要に応じて自由に操作する
* Not Servlet、あらゆるインフラストラクチャに適合（最小0.3 mでrpcアーキテクチャを実行）
* Web、Data、Job、Remoting、Cloudなどの開発シーンをサポートするIOC/AOPコンテナを自社で構築
* 集合Handler+ContextとListener+Messageの2つのスキーマパターン
* プラグイン式の拡張を強調し、拡張可能で切り替え可能、異なるアプリケーションシーンに対応
* ビジネスプラグインのホットプラグ、ホットダイヤルを許可する
* GraalVm Nativeパッケージのサポート
* Springではなく、Servletもなく、JavaEEにも関係ありません。新興独立したオープンエコ


## 生態構造図：

<img src="solon_schema.png" width="700" />

## こんにちは世界：

```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>2.2.11-SNAPSHOT</version>   
</parent>

<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
    </dependency>
</dependencies>
```

```java
@SolonMain
public class App{
    public static void main(String[] args){
        Solon.start(App.class, args, app->{
            //Handler mode：
            app.get("/hello",(c)->c.output("Hello world!"));
        });
    }
}

//Controller mode：(mvc or rest-api)
@Controller
public class HelloController{
    //Socketメソッドタイプの修飾
    @Socket
    @Mapping("/mvc/hello")
    public String hello(String name){
        return "Hello " + name;
    }
}

//Remoting mode：(rpc)
@Mapping("/rpc/")
@Remoting
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(){
        return "Hello world!";
    }
}
```


## メインフレームワークおよび高速統合開発パッケージ：

###### メインフレーム：

| コンポーネントパッケージ                    | 説明                          |
|------------------------|-----------------------------|
| org.noear:solon-parent | 依存バージョン管理                      |
| org.noear:solon        | メインフレーム                         |
| org.noear:nami         | 随伴フレームワーク（solon remotingのクライアントとして） |

###### 開発パッケージと相互関係の迅速な統合：

| コンポーネントパッケージ                       | 説明                                                          |
|---------------------------|-------------------------------------------------------------|
| org.noear:solon-lib       | 基盤統合パッケージの迅速な開発                                             |
| org.noear:solon-api       | solon-lib + jlhttp boot；高速開発インタフェースアプリケーション                 |
| org.noear:solon-web       | solon-api + freemarker + sessionstate；高速開発WEBアプリケーション       |
| org.noear:solon-beetl-web | solon-api + beetl + beetlsql + sessionstate；高速開発WEBアプリケーション |
| org.noear:solon-enjoy-web | solon-api + enjoy + arp + sessionstate；高速開発WEBアプリケーション      |
| org.noear:solon-rpc       | solon-api + nami；高速開発RPCアプリケーション                            |
| org.noear:solon-cloud     | solon-rpc + consul；マイクロサービスアプリケーションの迅速な開発                                |


## 公式サイトおよび関連例：

* 公式サイト：[https://solon.noear.org](https://solon.noear.org)
* 公式サイトのデモ：[https://gitee.com/noear/solon-examples](https://gitee.com/noear/solon-examples)
* プロジェクトのシングルテスト：[__test](./__test/) 
* プロジェクトの詳細機能の例：[solon_demo](https://gitee.com/noear/solon_demo) 、 [solon_api_demo](https://gitee.com/noear/solon_api_demo)  、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo) 、 [solon_auth_demo](https://gitee.com/noear/solon_auth_demo)

## 特にJetBrainsのオープンソースプロジェクトへのサポートに感謝します：

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

