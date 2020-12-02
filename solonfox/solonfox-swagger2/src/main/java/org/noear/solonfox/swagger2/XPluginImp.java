package org.noear.solonfox.swagger2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.models.*;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.RouteTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class XPluginImp implements Plugin {
    Swagger swagger = new Swagger();

    @Override
    public void start(SolonApp app) {
        if (app.source().getAnnotation(EnableSwagger2.class) == null) {
            return;
        }

        Aop.context().beanBuilderAdd(ApiModel.class, (clz, wrap, anno) -> {
            ModelImpl model = new ModelImpl();
            model.type(clz.getName());
            swagger.addDefinition(clz.getName(), model);
        });

        Aop.context().beanBuilderAdd(Api.class, (clz, wrap, anno) -> {
            Tag tag = new Tag();
            tag.name(clz.getName());

            swagger.addTag(tag);
        });

        Aop.beanOnloaded(this::onAppLoadEnd);

        app.get("/v2/api-doc",(c)->{
            c.outputAsJson(ONode.stringify(swagger));
        });

    }

    private void onAppLoadEnd() {
        Info info = Aop.get(Info.class);

        if (info != null) {
            swagger.info(info);
        }

        swagger.host("127.0.0.1");
        swagger.basePath("/");

        buildTags();

        buildPaths();
    }

    private void buildTags() {
        Aop.context().beanForeach((bw) -> {
            if (bw.annotationGet(Controller.class) != null) {
                Tag tag = new Tag();
                tag.name(bw.clz().getName());

                swagger.addTag(tag);
            }
        });
    }

    private void buildPaths() {
        Map<String, Path> pathMap = new LinkedHashMap<>();

        for (RouteTable.Route<Handler> route : Solon.global().router().main()) {
            if (route.target instanceof Action) {
                Action action = (Action) route.target;

                Path path = new Path();
                for (java.lang.reflect.Parameter p0 : action.method().getParameters()) {
                    Parameter p1 = (route.method == MethodType.GET ? new QueryParameter() : new FormParameter());
                    p1.setName(p0.getName());

                    path.addParameter(p1);
                }

                pathMap.put(route.path, path);
            }
        }

        swagger.setPaths(pathMap);
    }
}
