package features.nami;

import org.noear.nami.annotation.NamiBody;
import org.noear.nami.annotation.NamiMapping;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
public interface GitHub {
    @NamiMapping("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(String owner, String repo);

    @NamiMapping("POST /repos/{owner}/{repo}/issues")
    void createIssue(@NamiBody Issue issue, String owner, String repo);

    default String hello(){
        return "hello";
    }
}
