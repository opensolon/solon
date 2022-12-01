
```java
@Configuration
public class Config {
    //如果有配置
    @Bean
    public Scheduler initQuartz(@Inject("${demo.quartz}") StdSchedulerFactory factory){
        return factory.getScheduler();
    }

    //如果没有配置
    @Bean
    public Scheduler initQuartz(){
        SchedulerFactory factory = new StdSchedulerFactory();
        return factory.getScheduler();
    }
}
```