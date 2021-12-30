package org.noear.solon.extend.sureness.support;

import com.usthe.sureness.subject.Subject;
import com.usthe.sureness.subject.SubjectCreate;
import com.usthe.sureness.subject.support.DigestSubject;
import com.usthe.sureness.util.SurenessConstant;
import org.noear.solon.core.handle.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * the subject creator support creating DigestSubject
 * only support solon HTTP request
 * @author tomsun28
 * @date 2021/5/7 21:14
 */
public class DigestSubjectSolonCreator implements SubjectCreate {

    private static final Logger logger = LoggerFactory.getLogger(DigestSubjectSolonCreator.class);

    private static final String USERNAME = "username";
    private static final String NONCE = "nonce";
    private static final String QOP = "qop";
    private static final String REALM = "realm";
    private static final String NC = "nc";
    private static final String CNONCE = "cnonce";
    private static final String RESPONSE = "response";
    private static final String URI = "uri";
    private static final int FILED_SIZE = 2;
    private static final String SPLIT = "\"";

    @Override
    public boolean canSupportSubject(Object context) {
        if (context instanceof Context) {
            String authorization = ((Context)context).header(SurenessConstant.AUTHORIZATION);
            return authorization == null || authorization.startsWith(SurenessConstant.DIGEST);
        } else {
            return false;
        }
    }

    @Override
    public Subject createSubject(Object context) {
        String authorization = ((Context)context).header(SurenessConstant.AUTHORIZATION);
        if (authorization == null) {
            return new DigestSubject();
        } else {
            // digest auth
            String digestAuth = authorization.replace(SurenessConstant.DIGEST, "").trim();

            try {
                final Map<String, String> digestMap = new HashMap<>(8);

                Arrays.stream(digestAuth.split(",")).forEach(auth -> {
                    String[] tmpArr = auth.trim().split("=");
                    if (tmpArr.length == FILED_SIZE) {
                        String authValue = tmpArr[1].trim();
                        if (authValue.startsWith(SPLIT) && authValue.endsWith(SPLIT)) {
                            authValue = authValue.substring(1, authValue.length() - 1);
                        }
                        digestMap.put(tmpArr[0].trim(), authValue);
                    }
                });

                String username = digestMap.get(USERNAME);
                String response = digestMap.get(RESPONSE);
                String realm = digestMap.get(REALM);
                String uri = digestMap.get(URI);
                String nonce = digestMap.get(NONCE);
                String nc = digestMap.get(NC);
                String cNonce = digestMap.get(CNONCE);
                String qop =  digestMap.get(QOP);

                if (username == null || response == null || realm == null || uri == null
                        || nonce == null || nc == null || cNonce == null) {
                    logger.debug("can not create digest subject due some need field is null");
                    return null;
                }

                String remoteHost = ((Context) context).ip();
                String requestUri = ((Context) context).pathNew();
                String requestType = ((Context) context).method();
                String targetUri = requestUri.concat("===").concat(requestType).toLowerCase();
                return DigestSubject.builder(username, response)
                        .setRealm(realm).setUri(uri).setNonce(nonce)
                        .setNc(nc).setCnonce(cNonce).setQop(qop).setHttpMethod(requestType.toUpperCase())
                        .setRemoteHost(remoteHost).setTargetUri(targetUri)
                        .build();
            } catch (Exception e) {
                logger.info("create digest subject error happen, due {}", e.getMessage(), e);
                return null;
            }
        }
    }
}