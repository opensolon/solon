package features.dso;

import features.model.Author;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;

@Component
public class AuthorService {
    public Author findById(int id) {
        Author author = new Author();
        author.setId(id);
        if(id==1) {
            author.setName("悟纤");
            author.setPhoto("/img/1.png");
        }else if(id==2) {
            author.setName("悟空");
            author.setPhoto("/img/2.png");
        }
        return author;
    }

}
