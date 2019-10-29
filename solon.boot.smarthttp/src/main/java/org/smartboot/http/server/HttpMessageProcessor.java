package org.smartboot.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.exception.HttpException;
import org.smartboot.http.server.decode.Http11Request;
import org.smartboot.http.server.decode.HttpRequestProtocol;
import org.smartboot.http.server.handle.HttpHandle;
import org.smartboot.http.server.handle.RouteHandle;
import org.smartboot.http.server.handle.http11.RFC2612RequestHandle;
import org.smartboot.http.server.http11.DefaultHttpResponse;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;

/**
 * @author 三刀
 * @version V1.0 , 2018/6/10
 */
public class HttpMessageProcessor implements MessageProcessor<Http11Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMessageProcessor.class);
    private static byte[] b = ("HTTP/1.1 200 OK\r\n" +
            "Server:smart-socket\r\n" +
            "Connection:keep-alive\r\n" +
            "Content-Length:12\r\n" +
            "Date:Wed, 11 Apr 2018 12:35:01 GMT\r\n\r\n" +
            "Hello World!").getBytes();
    private ThreadLocal<DefaultHttpResponse> RESPONSE_THREAD_LOCAL = null;
    /**
     * Http消息处理器
     */
    private HttpHandle processHandle;
    private RouteHandle routeHandle;

    public HttpMessageProcessor(String baseDir) {
        processHandle = new RFC2612RequestHandle();
        routeHandle = new RouteHandle(baseDir);
        processHandle.next(routeHandle);

        RESPONSE_THREAD_LOCAL = new ThreadLocal<DefaultHttpResponse>() {
            @Override
            protected DefaultHttpResponse initialValue() {
                return new DefaultHttpResponse();
            }
        };
    }

    public static void main(String[] args) {
        System.setProperty("smart-socket.server.pageSize", (1024 * 1024 * 5) + "");
//        System.setProperty("smart-socket.bufferPool.pageNum", 512 + "");
        System.setProperty("smart-socket.server.page.isDirect", "true");
//        System.setProperty("sun.nio.ch.maxCompletionHandlersOnStack","4");
        System.setProperty("smart-socket.session.writeChunkSize", (1024 * 4) + "");
        HttpMessageProcessor processor = new HttpMessageProcessor("./");
        processor.route("/plaintext", new HttpHandle() {
            byte[] body = "Hello World!".getBytes();

            @Override
            public void doHandle(HttpRequest request, HttpResponse response) throws IOException {
                response.setContentLength(body.length);
                response.getOutputStream().write(body);
            }
        });
        AioQuickServer<Http11Request> server = new AioQuickServer<Http11Request>(8080, new HttpRequestProtocol(), processor);
        server.setReadBufferSize(1024 * 4);
//        server.setBannerEnabled(false);
//        server.setThreadNum(36);
//        server.setFairIO(true);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(AioSession<Http11Request> session, Http11Request request) {
        try {
//            if (true) {
//                session.writeBuffer().write(b);
//                request.rest();
//                return;
//            }
            DefaultHttpResponse httpResponse = RESPONSE_THREAD_LOCAL.get();
            httpResponse.init(session.writeBuffer());
//            boolean isKeepAlive = StringUtils.equalsIgnoreCase(HttpHeaderConstant.Values.KEEPALIVE, request.getHeader(HttpHeaderConstant.Names.CONNECTION));
            try {
                //用ab进行测试时需要带上该响应
//                if (isKeepAlive) {
//                    httpResponse.setHeader(HttpHeaderConstant.Names.CONNECTION, HttpHeaderConstant.Values.KEEPALIVE);
//                }
                processHandle.doHandle(request, httpResponse);
            } catch (HttpException e) {
                e.printStackTrace();
                httpResponse.setHttpStatus(HttpStatus.valueOf(e.getHttpCode()));
                httpResponse.getOutputStream().write(e.getDesc().getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                httpResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                httpResponse.getOutputStream().write(e.fillInStackTrace().toString().getBytes());
            }
//
            httpResponse.getOutputStream().close();

//
//            if (!isKeepAlive || httpResponse.getHttpStatus() != HttpStatus.OK) {
//                LOGGER.info("will close session");
//                session.close(false);
//            }
        } catch (IOException e) {
            LOGGER.error("IO Exception", e);
        }
        request.rest();
    }

    @Override
    public void stateEvent(AioSession<Http11Request> session, StateMachineEnum stateMachineEnum, Throwable throwable) {
//        LOGGER.info(stateMachineEnum+" "+session.getSessionID());
//        if (throwable != null) {
//            LOGGER.error("",throwable);
//            System.exit(1);
//        }
        switch (stateMachineEnum) {
            case NEW_SESSION:
//                LOGGER.info("new connection:{}");
//                System.out.println("newSession");
                session.setAttachment(new Http11Request());
                break;
            case PROCESS_EXCEPTION:
                LOGGER.error("process request exception", throwable);
                session.close();
                break;
//            case INPUT_SHUTDOWN:
//                LOGGER.error("inputShutdown", throwable);
//                break;
//            case OUTPUT_EXCEPTION:
//                LOGGER.error("", throwable);
//                break;
//            case INPUT_EXCEPTION:
//                LOGGER.error("",throwable);
//                break;
//            case SESSION_CLOSED:
//                System.out.println("closeSession");
//                LOGGER.info("connection closed:{}");
//                break;
            case DECODE_EXCEPTION:
                throwable.printStackTrace();
                break;
//                default:
//                    System.out.println(stateMachineEnum);
        }
    }

    public void route(String urlPattern, HttpHandle httpHandle) {
        routeHandle.route(urlPattern, httpHandle);
    }
}
