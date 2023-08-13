package graphql.solon.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class ClassPathResource implements Resource {

    private final URL url;

    public ClassPathResource(URL url) {
        this.url = url;
    }

    @Override
    public boolean exists() {
        return Objects.nonNull(this.url);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = null;
        if (Objects.nonNull(this.url)) {
            is = this.url.openStream();
        }
        if (Objects.isNull(is)) {
            throw new FileNotFoundException(
                    getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    public String getDescription() {
        StringBuilder builder = new StringBuilder("class path resource [");
        String pathToUse = this.url.toString();
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        builder.append(pathToUse);
        builder.append(']');
        return builder.toString();
    }
}
