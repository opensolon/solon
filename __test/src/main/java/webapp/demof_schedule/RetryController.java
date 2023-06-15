package webapp.demof_schedule;


import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Controller
public class RetryController {
    @Inject
    private RetryService helloService;

    @Mapping("retry")
    public String f1() {
        return helloService.m1("qwe");
    }
}
