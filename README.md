
[![Maven Central](https://img.shields.io/maven-central/v/org.noear/solon.svg)](https://mvnrepository.com/search?q=g:org.noear%20AND%20solon)

` QQ交流群：22200020 `

# Solon for java

A plug-in Java micro development framework。

#### Faster, smaller, freer！

Support jdk8 +; Core frame 0.1Mb; Combining different plug-ins to meet different needs; Convenient customization; Rapid development of。


* Restrained, concise and open
* Unified development experience of HTTP, WebSocket and Socket signals (commonly known as three sources in one)
* Support annotation and manual two modes, free control as needed
* Not Servlets, which can be adapted to any underlying communication framework (so: RPC architecture runs at minimum 0.2Mb)
* Self-built IOC & AOP container, support REST API, MVC, Job, Remoting, MicoService and other development
* Set Handler + Context and Listener + Message architecture patterns; Emphasis on plug-in extension; Adapt to different application scenarios
* Plug-ins are extensible and switchable: startup plug-in, extension plug-in, serialization plug-in, data plug-in, session state plug-in, view plug-in (coexist), etc.
* The use of Spring Boot feels similar to the migration cost is low


### Hello world：

```java
//Handler mode：
public class App{
    public static void main(String[] args){
        SolonApp app = Solon.start(App.class,args);
        
        app.get("/",(c)->c.output("Hello world!"));
    }
}

//Controller mode：：(mvc or rest-api)
@Controller
public class App{
    public static void main(String[] args){
        Solon.start(App.class,args);
    }
  
    @Get
    @Mapping("/")
    public Object hello(Context c){
        return "Hello world!";  
    }
}

//Remoting mode：(rpc)
@Mapping("/")
@Socket
@Remoting
public class App implements HelloService{
    public static void main(String[] args){
        Solon.start(App.class,args);
    }

    @Override
    public String hello(){
        return "Hello world!";
    }
}
```


### Main framework and rapid integration development package：

###### Main frame

| component or plugin | description |
| --- | --- |
| org.noear:solon-parent | Framework versioning |
| org.noear:solon | Main frame |
| org.noear:nami | Companion Framework (as a client to Solon Remoting) |

###### Rapid integration of development kits

| component or plugin | description |
| --- | --- |
| org.noear:solon-lib | Rapid development of basic integration packages |
| org.noear:solon-api | solon-lib + http boot；Rapid development of interface applications |
| org.noear:solon-web | solon-api + freemarker + sessionstate；Rapid development of WEB applications |
| org.noear:solon-rpc | solon-api + nami；Rapid development of remoting applications |
| org.noear:solon-cloud | solon-rpc + consul；Rapid development of microservice applications |

### Attachment 1: A quick understanding of Solon's materials：

#### [《What is the difference between Solon and SpringBoot?》](https://my.oschina.net/noear/blog/4863844)

#### [《Solon Cloud distributed service development suite manifest, feel different from Spring Cloud》](https://my.oschina.net/noear/blog/5039169)

#### [《Solon's ideas and architecture notes》](https://my.oschina.net/noear/blog/4980834)

#### [《Solon Ecological Plugins List》](https://my.oschina.net/noear/blog/5053423)

#### [《Introduction to the Solon framework》](https://my.oschina.net/noear/blog/4784513)


### Attachment 2: Examples and articles
* Within the project：[_test](./_test/) 和 [_demo](./_demo/)
* More examples：[solon_demo](https://gitee.com/noear/solon_demo) 、 [solon_rpc_demo](https://gitee.com/noear/solon_rpc_demo) 、 [solon_socketd_demo](https://gitee.com/noear/solon_socketd_demo) 、 [solon_cloud_demo](https://gitee.com/noear/solon_cloud_demo)
* More articles：[https://www.cnblogs.com/noear/](https://www.cnblogs.com/noear/)

### Attachment 3: Quick Start Examples
* Web example（mvc）
```xml
<parent>
    <groupId>org.noear</groupId>
    <artifactId>solon-parent</artifactId>
    <version>1.4.3</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-web</artifactId>
    </dependency>
</dependencies>

```
```
//Resource path specification (no configuration)
resources/application.properties（or application.yml） #Configuration files for the application
resources/static/ #Is the static file root target
resources/WEB-INF/view/ #The root target of the view file (supports coexistence of multiple views)

//Debug mode：
Add startup parameters：-debug=1
```
```java
public class App{
    public static void main(String[] args){
        Solon.start(App.class, args);
    }
}

/*
 * mvc controller
 */
@Controller
public class DemoController{
    //for http
    @Mapping("/hallo/{u_u}")
    public ModelAndView hallo(String u_u){
        return new ModelAndView("hallo");
    }
    
    /*
    //for web socket （Need to add: solon.boot.websocket plug-in）
    @Mapping(value="/hallo/{u_u}", method = MethodType.WEBSOCKET)
    public ModelAndView hallo_ws(String u_u){
        return new ModelAndView("hallo");
    }
    */
}
```

* Remoting example（rpc）

```java
// - interface : Defines the interface
public interface DemoService{
    void setName(Integer user_id, String name);
}

// - server : Implementing an interface
@Mapping("/demo/*")
@Remoting
public class DemoServiceImp implements DemoService{
    public void setName(int user_id, String name){
        
    }
}

// - client - A simple example
//Injection pattern
//@NamiClient("http://127.0.0.1:8080/demo/") 
//DemoService client;

//Manual build mode
DemoService client = Nami.builder().url("http://127.0.0.1:8080/demo/").create(DemoService.class); 
client.setName(1,'');


```

* Get the application configuration
```java
//Manual mode
Solon.cfg().get("app_key"); //=>String
Solon.cfg().getInt("app_id",0); //=>int
Solon.cfg().getProp("xxx.datasource"); //=>Properties

//Injection pattern
@Configuration //or @Controller, or @Component
public class Config{
    @Inject("${app_key}")
    String app_key;

    @Inject("${app_title:Solon}")
    String app_title;
}
```

* Transaction and cache control (+ validation)
```java
@Valid
@Controller
public class DemoController{
    @Db
    BaseMapper<UserModel> userService;
    
    @NotZero("user_id")
    @CacheRemove(tags = "user_${user_id}")
    @Tran
    @Mapping("/user/update")
    public void udpUser(int user_id, UserModel user){
        userService.updateById(user);
    }

    @NotZero("user_id")
    @Cache(tags = "user_${user_id}")
    public UserModel getUser(int user_id){
        return userService.selectById(user_id);
    }
}
```

* File upload and output
```java
@Controller
public class DemoController{
    @Mapping("/file/upload")
    public void upload(UploadedFile file){
        IoUtil.save(file.content, "/data/file_" + file.name);
    }

    @Mapping("/file/down")
    public void down(Context ctx, String path){
        URL uri = Utils.getResource(path);

        ctx.contentType("json/text");
        ctx.output(uri.openStream());
    }
}
```

* Servlet annotations are still supported
```java
@WebFilter("/hello/*")
public class HelloFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.getWriter().write("Hello，I filtered you out");
    }
}
```

* Quartz Timing task
```java
@Quartz(cron7x = "0 0/1 * * * ? *")
public class HelloTask implements Runnable {
    public static void main(String[] args){
        Solon.start(QuartzRun2.class,args);
    }
    
    @Override
    public void run() {
        System.out.println("Hello world");
    }
}
```

* External extensions load the JAR
```
demoApp.jar             #The main program
ext/                    #Extensions directory
ext/ext.markdown.jar    #The MD format supports extension packs
```

* Single-linked bidirectional RPC (after the server on the client chain, form a bidirectional RPC)
```java 
//server
@Mapping(value = "/demoh/rpc", method = MethodType.SOCKET)
@Remoting
public class HelloRpcServiceImpl implements HelloRpcService {
    public String hello(String name) {
        //Here, you can create an RPC service that connects to the client based on the client session
        NameRpcService rpc = SocketD.create(Context.current(), NameRpcService.class);

        String name2 = rpc.name(name);

        return "name=" + name;
    }
}

//client
HelloRpcService rpc = SocketD.create("tcp://localhost:"+_port, HelloRpcService.class);

String rst = rpc.hello("noear");
```

* Solon cloud Configure service usage
```java
@Controller
public class DemoController {
    public static void main(String[] args){
        Solon.start(DemoController.class,args);
    }
    
    //Injection pattern
    @CloudConfig(value = "user.name", autoRefreshed = true)
    String userName;
    
    @Mapping("/")
    public void run() {
        //Manual mode
        userName = CloudClient.config().pull("user.name").value();
    }
}
```

* Solon cloud event bus usage
```java
//Event subscription and consumption
@CloudEvent("hello.demo")
public class DemoEvent implements CloudEventHandler {
    @Override
    public boolean handler(Event event) throws Throwable {
        //Return to success
        return true;
    }
}

//Event production
CloudClient.event().publish(new Event("hello.demo", msg));
```


### Attachment 4: Plug-in development instructions
* Create a new Maven project
* Create a new java file: /{package name}/XPluginImp.java (implements XPlugin)
* Create a new properties file: /META-INF/solon/{package.properties}
*    Add configuration: solon.plugin={package name}.XPluginImp

### Attachment 5: Start sequence reference

* 1. Instantiate solon.global () and load the configuration
* 2. Load the extension folder
* 3. Scan and sort plug-ins
* 4. Run initialize function
* 5. Push AppInitEndEvent [event]
* 6. Run the plug-in
* 7. Push pluginLoadEndEvent [event]
* 8. Import Java beans (@import)
* 9. Scan and load the Java beans
* a. Push BeanLoadEndEvent [event]
* b. Load render printout
* c. Execute bean plus completion event
* d. Push PloadendEvent [event]
* e in the end

### Attachment 6: The number of concurrency per machine of HelloWorld [《helloworld_wrk_test》](https://github.com/noear/helloworld_wrk_test)

> * The machine：2017 macbook pro 13, i7, 16g, MacOS 10.15, jdk11
> * Test：wrk -t10 -c200 -d30s --latency "http://127.0.0.1:8080/"

|  solon 1.1.2 | Packet size | QPS | 
| -------- | -------- | -------- | 
| solon.boot.jlhttp(bio)     | 0.1m     | 4.7万左右     |
| solon.boot.jetty(nio, support servlet api)     | 1.8m     | 10.7万左右     | 
| solon.boot.undertow(nio, support servlet api)     | 4.2m     | 11.3万左右     | 
| solon.boot.smarthttp(aio)     | 0.3m     | 12.4万左右     | 


| spring boot 2.3.3  | Packet size |  QPS  | 
| -------- | -------- | -------- |
| spring-boot-starter-tomcat   | 16.1m |  3.2万左右  | 
| spring-boot-starter-jetty | 16m | 3.7万左右 |
| spring-boot-starter-undertow | 16.8m | 4.4万左右 |
