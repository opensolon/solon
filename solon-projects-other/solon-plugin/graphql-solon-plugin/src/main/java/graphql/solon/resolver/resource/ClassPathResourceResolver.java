package graphql.solon.resolver.resource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.ScanUtil;
import graphql.solon.resource.ClassPathResource;
import graphql.solon.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class ClassPathResourceResolver implements GraphqlResourceResolver {

    private static Logger log = LoggerFactory.getLogger(ClassPathResourceResolver.class);

    private List<String> locations;
    private List<String> fileExtensions;

    public ClassPathResourceResolver(List<String> locations, List<String> fileExtensions) {
        this.locations = locations;
        this.fileExtensions = fileExtensions;
    }

    @Override
    public Set<Resource> getGraphqlResource() {
        Set<Resource> resources = this.locations.stream()
                .map(location -> {
                    Set<String> scanResult = ScanUtil
                            .scan(location, (path) -> this.fileExtensions.stream().anyMatch(
                                    path::endsWith));
                    log.debug("[{}]文件夹下共扫描到[{}]个定义文件", location, scanResult.size());
                    return scanResult;
                }).flatMap(Set::stream)
                .map(ResourceUtil::getResource)
                .map(ClassPathResource::new)
                .collect(Collectors.toSet());

        log.debug("共扫描到[{}]个定义文件", resources.size());

        return resources;
    }
}
