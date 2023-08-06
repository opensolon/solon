package org.noear.solon.extend.sureness.support;

import com.usthe.sureness.subject.Subject;
import com.usthe.sureness.subject.SubjectCreate;
import com.usthe.sureness.subject.support.PasswordSubject;
import org.noear.solon.core.handle.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * the creator to create PasswordSubject(basic auth)
 * only support solon HTTP request
 * @author tomsun28
 * @date 2021/5/7 20:49
 */
public class BasicSubjectSolonCreator implements SubjectCreate {

    private static final Logger logger = LoggerFactory.getLogger(BasicSubjectSolonCreator.class);

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic";
    private static final int COUNT_2 = 2;

    @Override
    public boolean canSupportSubject(Object context) {
        // ("Authorization", "Basic YWRtaW46YWRtaW4=")        --- basic auth
        if (context instanceof Context) {
            String authorization = ((Context)context).header(AUTHORIZATION);
            return authorization != null && authorization.startsWith(BASIC);
        } else {
            return false;
        }
    }

    @Override
    public Subject createSubject(Object context) {
        String authorization = ((Context)context).header(AUTHORIZATION);
        if (authorization == null) {
            return null;
        }

        //basic auth
        String basicAuth = authorization.replace(BASIC, "").trim();
        basicAuth = new String(Base64.getDecoder().decode(basicAuth), StandardCharsets.UTF_8);
        String[] auth = basicAuth.split(":");
        if (auth.length != COUNT_2) {
            if (logger.isInfoEnabled()) {
                logger.info("can not create basic auth PasswordSubject by this request message");
            }
            return null;
        }

        String username = auth[0];
        if (username == null || "".equals(username)) {
            if (logger.isInfoEnabled()) {
                logger.info("can not create basic auth PasswordSubject by this request message, appId can not null");
            }
            return null;
        }

        String password = auth[1];
        String remoteHost = ((Context) context).ip();
        String requestUri = ((Context) context).pathNew();
        String requestType = ((Context) context).method();
        String targetUri = requestUri.concat("===").concat(requestType).toLowerCase();

        return PasswordSubject.builder(username, password)
                .setRemoteHost(remoteHost)
                .setTargetResource(targetUri)
                .build();
    }
}