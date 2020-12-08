package org.noear.solonfox.swagger2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.models.*;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.route.RouteTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class XPluginImp implements Plugin {
    protected static Swagger swagger = new Swagger();

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


        app.beanScan(SwaggerController.class);

        Aop.beanOnloaded(this::onAppLoadEnd);
    }

    private void onAppLoadEnd() {
        Info info = Aop.get(Info.class);

        if (info != null) {
            swagger.info(info);
        }

        swagger.host("localhost:" + Solon.global().port());
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
                {
                    switch (route.method) {
                        case GET: {
                            path.get(buildPathPperation(route, true));
                            break;
                        }
                        case POST: {
                            path.post(buildPathPperation(route, false));
                            break;
                        }
                        case PUT: {
                            path.put(buildPathPperation(route, false));
                            break;
                        }
                        case DELETE: {
                            path.delete(buildPathPperation(route, false));
                            break;
                        }
                        case PATCH: {
                            path.patch(buildPathPperation(route, false));
                            break;
                        }
                        case HTTP: {
                            //path.get(buildPathPperation(route, true));
                            path.post(buildPathPperation(route, false));
                            //path.put(buildPathPperation(route, false));
                            //path.delete(buildPathPperation(route, false));
                            //path.patch(buildPathPperation(route, false));
                            break;
                        } default:{
                            path.post(buildPathPperation(route, false));
                        }
                    }
                }

                pathMap.put(route.path, path);
            }
        }

        swagger.setPaths(pathMap);
    }

    private Operation buildPathPperation(RouteTable.Route<Handler> route, boolean isGet) {
        Action action = (Action) route.target;


        Operation operation = new Operation();
        operation.addTag(action.bean().clz().getName());

        if (Utils.isNotEmpty(action.produces())) {
            operation.addProduces(action.produces());
        }else{
            operation.addProduces("*/*");
        }

        if (Utils.isNotEmpty(action.consumes())) {
            operation.addConsumes(action.consumes());
        }

        for (java.lang.reflect.Parameter p0 : action.method().getParameters()) {
            String n1 = "{" + p0.getName() + "}";
            Parameter p1 = null;

            if (route.path.indexOf(n1) > 0) {
                p1 = new PathParameter();
            } else {
                if (isGet) {
                    p1 = new QueryParameter();
                } else {
                    p1 = new FormParameter();
                }
            }

            p1.setName(p0.getName());

            operation.addParameter(p1);
        }

        return operation;
    }
}
