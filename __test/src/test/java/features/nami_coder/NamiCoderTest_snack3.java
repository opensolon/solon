/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package features.nami_coder;

import features._model.UserModel;
import org.junit.jupiter.api.Test;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.nami.Result;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author noear 2021/5/20 created
 */
public class NamiCoderTest_snack3 {
    //String json_err = "{\"@type\":\"java.lang.IllegalArgumentException\",\"localizedMessage\":\"coin_type,amount,tran_num\",\"message\":\"coin_type,amount,tran_num\",\"stackTrace\":[{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.srww.uapi.validation.ValidatorFailureHandlerSrwwImp\",\"fileName\":\"ValidatorFailureHandlerSrwwImp.java\",\"lineNumber\":51,\"methodName\":\"onFailure\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":200,\"methodName\":\"failureDo\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":186,\"methodName\":\"validateDo\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":160,\"methodName\":\"validate\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":146,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ContextValidateInterceptor\",\"fileName\":\"ContextValidateInterceptor.java\",\"lineNumber\":16,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":184,\"methodName\":\"lambda$invoke0$0\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":239,\"methodName\":\"handleDo\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":182,\"methodName\":\"invoke0\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":161,\"methodName\":\"invoke\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":140,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.route.RouterHandler\",\"fileName\":\"RouterHandler.java\",\"lineNumber\":68,\"methodName\":\"handleOne\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.route.RouterHandler\",\"fileName\":\"RouterHandler.java\",\"lineNumber\":42,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.SolonApp\",\"fileName\":\"SolonApp.java\",\"lineNumber\":501,\"methodName\":\"doFilter\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.FilterChainNode\",\"fileName\":\"FilterChainNode.java\",\"lineNumber\":23,\"methodName\":\"doFilter\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.SolonApp\",\"fileName\":\"SolonApp.java\",\"lineNumber\":490,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.SolonApp\",\"fileName\":\"SolonApp.java\",\"lineNumber\":512,\"methodName\":\"tryHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.servlet.SolonServletHandler\",\"fileName\":\"SolonServletHandler.java\",\"lineNumber\":30,\"methodName\":\"service\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"javax.servlet.http.HttpServlet\",\"fileName\":\"HttpServlet.java\",\"lineNumber\":750,\"methodName\":\"service\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.servlet.ServletHolder\",\"fileName\":\"ServletHolder.java\",\"lineNumber\":791,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.servlet.ServletHandler\",\"fileName\":\"ServletHandler.java\",\"lineNumber\":550,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":233,\"methodName\":\"nextHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.session.SessionHandler\",\"fileName\":\"SessionHandler.java\",\"lineNumber\":1624,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":233,\"methodName\":\"nextHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ContextHandler\",\"fileName\":\"ContextHandler.java\",\"lineNumber\":1435,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":188,\"methodName\":\"nextScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.servlet.ServletHandler\",\"fileName\":\"ServletHandler.java\",\"lineNumber\":501,\"methodName\":\"doScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.session.SessionHandler\",\"fileName\":\"SessionHandler.java\",\"lineNumber\":1594,\"methodName\":\"doScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":186,\"methodName\":\"nextScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ContextHandler\",\"fileName\":\"ContextHandler.java\",\"lineNumber\":1350,\"methodName\":\"doScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":141,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.HandlerWrapper\",\"fileName\":\"HandlerWrapper.java\",\"lineNumber\":127,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.Server\",\"fileName\":\"Server.java\",\"lineNumber\":516,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpChannel\",\"fileName\":\"HttpChannel.java\",\"lineNumber\":388,\"methodName\":\"lambda$handle$1\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpChannel\",\"fileName\":\"HttpChannel.java\",\"lineNumber\":633,\"methodName\":\"dispatch\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpChannel\",\"fileName\":\"HttpChannel.java\",\"lineNumber\":380,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpConnection\",\"fileName\":\"HttpConnection.java\",\"lineNumber\":279,\"methodName\":\"onFillable\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.io.AbstractConnection$ReadCallback\",\"fileName\":\"AbstractConnection.java\",\"lineNumber\":311,\"methodName\":\"succeeded\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.io.FillInterest\",\"fileName\":\"FillInterest.java\",\"lineNumber\":105,\"methodName\":\"fillable\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.io.ChannelEndPoint$1\",\"fileName\":\"ChannelEndPoint.java\",\"lineNumber\":104,\"methodName\":\"run\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.util.thread.QueuedThreadPool\",\"fileName\":\"QueuedThreadPool.java\",\"lineNumber\":882,\"methodName\":\"runJob\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.util.thread.QueuedThreadPool$Runner\",\"fileName\":\"QueuedThreadPool.java\",\"lineNumber\":1036,\"methodName\":\"run\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"className\":\"java.lang.Thread\",\"fileName\":\"Thread.java\",\"lineNumber\":832,\"methodName\":\"run\",\"moduleName\":\"java.base\",\"moduleVersion\":\"14.0.2\",\"nativeMethod\":false}]}";
    String json_usr = "{\"id\":1,\"name\":\"noear\",\"sex\":1,\"date\":\"2023-09-24 15:51:35\"}";
    String json_usr2 = "{\"id\":1,\"name\":\"noear\",\"sex\":1,\"date\":\"1744102552378\"}";
    String json_usr_ary = "[{\"@type\":\"features._model.UserModel\",\"id\":1,\"name\":\"noear\",\"sex\":1}]";

//    @Test
//    public void test_snack3_err() {
//        //err
//        Result err_rst = new Result(200, json_err.getBytes(StandardCharsets.UTF_8));
//        try {
//            Object rst = SnackDecoder.instance.decode(err_rst, UserModel.class);
//            if (rst instanceof IllegalArgumentException) {
//                assert true;
//                System.out.println("test_snack3::ok");
//                return;
//            }
//
//            assert false;
//        } catch (IllegalArgumentException e) {
//            assert true;
//            System.out.println("test_snack3::ok");
//        }
//    }

    @Test
    public void test_snack3_bean() {

        //bean
        Result usr_rst = new Result(200, json_usr.getBytes(StandardCharsets.UTF_8));
        Object usr_obj = SnackDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj instanceof UserModel;
        assert ((UserModel) usr_obj).id == 1;


        //bean list
        Result usr_rst_ary = new Result(200, json_usr_ary.getBytes(StandardCharsets.UTF_8));
        Object usr_obj_ary = SnackDecoder.instance.decode(usr_rst_ary, List.class);

        assert usr_obj_ary instanceof List;
        assert ((List<?>) usr_obj_ary).size() == 1;


        //null
        usr_rst = new Result(200, SnackEncoder.instance.encode(null));
        usr_obj = SnackDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj == null;
    }

    @Test
    public void test_snack3_bean2() {
        //bean
        Result usr_rst = new Result(200, json_usr2.getBytes(StandardCharsets.UTF_8));
        Object usr_obj = SnackDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj instanceof UserModel;
        assert ((UserModel) usr_obj).id == 1;
        assert ((UserModel) usr_obj).date.getTime() == 1744102552378L;
    }

}
