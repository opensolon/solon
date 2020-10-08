package features.dso;

import features.model.Author;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XInject;

@XBean
public class AuthorQuery implements GraphQLQueryResolver {
    @XInject
    private AuthorService authorService;


    public Author findAuthorById(int id) {
        return authorService.findById(id);
    }

}
