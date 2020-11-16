package features.dso;

import features.model.Author;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Inject;

@Bean
public class AuthorQuery implements GraphQLQueryResolver {
    @Inject
    private AuthorService authorService;


    public Author findAuthorById(int id) {
        return authorService.findById(id);
    }

}
