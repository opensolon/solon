package org.noear.solon.extend.sureness.support;

import com.usthe.sureness.subject.Subject;
import com.usthe.sureness.subject.SubjectCreate;
import com.usthe.sureness.subject.support.NoneSubject;
import org.noear.solon.core.handle.Context;

/**
 * the creator to create NoneSubject - no auth info
 * all request can be created a NoneSubject by NoneSubjectReactiveCreator
 * only support solon Context
 * @author tomsun28
 * @date 2021/5/7 20:51
 */
public class NoneSubjectSolonCreator implements SubjectCreate {
    @Override
    public boolean canSupportSubject(Object context) {
        return context instanceof Context;
    }

    @Override
    public Subject createSubject(Object context) {
        String remoteHost = ((Context) context).ip();
        String requestUri = ((Context) context).pathNew();
        String requestType = ((Context) context).method();
        String targetUri = requestUri.concat("===").concat(requestType).toLowerCase();

        return NoneSubject.builder().setRemoteHost(remoteHost)
                .setTargetUri(targetUri).build();
    }
}