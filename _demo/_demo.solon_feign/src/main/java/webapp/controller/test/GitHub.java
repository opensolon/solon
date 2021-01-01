package webapp.controller.test;

import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */

public interface GitHub {
    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);

    @RequestLine("POST /repos/{owner}/{repo}/issues")
    void createIssue(Issue issue, @Param("owner") String owner,@Param("repo") String repo);
}
