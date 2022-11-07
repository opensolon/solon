package features.nami;

import org.noear.nami.annotation.Body;
import org.noear.nami.annotation.Mapping;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
public interface GitHub {
    @Mapping("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(String owner, String repo);

    @Mapping("POST /repos/{owner}/{repo}/issues")
    void createIssue(@Body Issue issue, String owner, String repo);

    default String hello(){
        return "hello";
    }
}
