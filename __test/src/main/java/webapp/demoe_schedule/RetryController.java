package webapp.demoe_schedule;


import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Mapping("demoe")
@Controller
public class RetryController {
    @Inject
    private RetryService helloService;

    @Mapping("retry")
    public String f1() {
        return helloService.m1("qwe");
    }

    @Mapping("retry2")
    public String f2() {
        return helloService.m2("qwe");
    }
}
