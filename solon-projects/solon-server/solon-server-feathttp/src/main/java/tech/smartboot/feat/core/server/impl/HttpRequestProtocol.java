/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.impl;

import io.github.smartboot.socket.Protocol;
import io.github.smartboot.socket.transport.AioSession;
import tech.smartboot.feat.core.common.*;
import tech.smartboot.feat.core.common.exception.HttpException;
import tech.smartboot.feat.core.common.logging.Logger;
import tech.smartboot.feat.core.common.logging.LoggerFactory;
import tech.smartboot.feat.core.server.HttpHandler;
import tech.smartboot.feat.core.server.ServerOptions;
import tech.smartboot.feat.core.server.waf.WAF;

import java.nio.ByteBuffer;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class HttpRequestProtocol implements Protocol<HttpEndpoint> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestProtocol.class);
    private final ServerOptions options;
    private static final ByteTree.EndMatcher URI_END_MATCHER = endByte -> (endByte == ' ' || endByte == '?');

    public HttpRequestProtocol(ServerOptions options) {
        this.options = options;
    }

    @Override
    public HttpEndpoint decode(ByteBuffer byteBuffer, AioSession session) {
        HttpEndpoint request = session.getAttachment();
        int p = byteBuffer.position();
        boolean flag = decode(byteBuffer, request);
        request.decodeSize(byteBuffer.position() - p);
        return flag ? request : null;
    }

    private boolean decode(ByteBuffer byteBuffer, HttpEndpoint request) {
        DecodeContext decodeContext = request.getDecodeContext();
        switch (decodeContext.getState()) {
            case DecodeContext.STATE_METHOD: {
                ByteTree<?> method = FeatUtils.scanByteTree(byteBuffer, ByteTree.SP_END_MATCHER, options.getByteCache());
                if (method == null) {
                    break;
                }
                request.setMethod(method.getStringValue());
                decodeContext.setState(DecodeContext.STATE_URI);
                WAF.methodCheck(options, request);
            }
            case DecodeContext.STATE_URI: {
                ByteTree<HttpHandler> uriTreeNode = FeatUtils.scanByteTree(byteBuffer, URI_END_MATCHER, options.getUriByteTree());
                if (uriTreeNode == null) {
                    break;
                }
                request.setUri(uriTreeNode.getStringValue());
                if (uriTreeNode.getAttach() != null) {
                    request.setServerHandler(uriTreeNode.getAttach());
                }
                WAF.checkUri(options, request);
                switch (byteBuffer.get(byteBuffer.position() - 1)) {
                    case FeatUtils.SP:
                        decodeContext.setState(DecodeContext.STATE_PROTOCOL_DECODE);
                        break;
                    case '?':
                        decodeContext.setState(DecodeContext.STATE_URI_QUERY);
                        break;
                    default:
                        throw new HttpException(HttpStatus.BAD_REQUEST);
                }
                return decode(byteBuffer, request);
            }
            case DecodeContext.STATE_URI_QUERY: {
                // 兼容 /? HTTP/1.1 这类场景
                if (byteBuffer.hasRemaining() && byteBuffer.get(byteBuffer.position()) == FeatUtils.SP) {
                    byteBuffer.position(byteBuffer.position() + 1);
                    request.setQueryString("");
                    decodeContext.setState(DecodeContext.STATE_PROTOCOL_DECODE);
                    return decode(byteBuffer, request);
                }
                ByteTree<?> query = FeatUtils.scanByteTree(byteBuffer, ByteTree.SP_END_MATCHER, options.getByteCache());
                if (query == null) {
                    break;
                }
                request.setQueryString(query.getStringValue());
                decodeContext.setState(DecodeContext.STATE_PROTOCOL_DECODE);
            }
            case DecodeContext.STATE_PROTOCOL_DECODE: {
                if (byteBuffer.remaining() < 12) {
                    break;
                }
                long httpVersion = byteBuffer.getLong();
                if (httpVersion == 5211883372140375601L) {
                    request.setProtocol(HttpProtocol.HTTP_11);
                } else if (httpVersion == 5211883372140375600L) {
                    request.setProtocol(HttpProtocol.HTTP_10);
                } else if (httpVersion == 5211883372140441136L) {
                    request.setProtocol(HttpProtocol.HTTP_2);
                } else if (byteBuffer.get(byteBuffer.position() - 8) == FeatUtils.SP) {
                    byteBuffer.position(byteBuffer.position() - 7);
                    return decode(byteBuffer, request);
                } else {
                    byte[] bytes = new byte[byteBuffer.limit()];
                    byteBuffer.position(0);
                    byteBuffer.get(bytes);
                    LOGGER.error("Unsupported HTTP version, remote:{}, method:{}, uri:{} , data:\r\n{} ", request.getRemoteAddr(), request.getMethod(), request.getUri(), new String(bytes));
                    throw new HttpException(HttpStatus.BAD_REQUEST);
                }
                if (byteBuffer.getShort() != 3338) {
                    throw new HttpException(HttpStatus.BAD_REQUEST);
                }
                decodeContext.setState(DecodeContext.STATE_HEADER_END_CHECK);
            }
            // header结束判断
            case DecodeContext.STATE_HEADER_END_CHECK: {
                //header解码结束
                if (byteBuffer.get() == FeatUtils.CR) {
                    if (byteBuffer.get() != FeatUtils.LF) {
                        throw new HttpException(HttpStatus.BAD_REQUEST);
                    }
                    decodeContext.setState(DecodeContext.STATE_HEADER_CALLBACK);
                    return true;
                }
                byteBuffer.position(byteBuffer.position() - 1);
                if (request.getHeaderSize() < options.getHeaderLimiter()) {
                    decodeContext.setState(DecodeContext.STATE_HEADER_NAME);
                } else {
                    decodeContext.setState(DecodeContext.STATE_HEADER_IGNORE);
                    return decode(byteBuffer, request);
                }
            }
            // header name解析
            case DecodeContext.STATE_HEADER_NAME: {
                ByteTree<HeaderName> name = FeatUtils.scanByteTree(byteBuffer, ByteTree.COLON_END_MATCHER, options.getHeaderNameByteTree());
                if (name == null) {
                    break;
                }
                decodeContext.setHeaderName(name);
                decodeContext.setState(DecodeContext.STATE_HEADER_VALUE);
            }
            // header value解析
            case DecodeContext.STATE_HEADER_VALUE: {
                ByteTree<?> value = FeatUtils.scanByteTree(byteBuffer, ByteTree.CR_END_MATCHER, options.getByteCache());
                if (value == null) {
                    if (byteBuffer.remaining() == byteBuffer.capacity()) {
                        throw new HttpException(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, "The length of the value of header <u>" + decodeContext.getHeaderName().getStringValue() + "</u> exceeds the read buffer.");
                    }
                    break;
                }
                HeaderName headerName = decodeContext.getHeaderName().getAttach();
                if (headerName != null) {
                    request.addHeader(headerName.getLowCaseName(), decodeContext.getHeaderName().getStringValue(), value.getStringValue());
                } else {
                    request.addHeader(decodeContext.getHeaderName().getStringValue().toLowerCase(), decodeContext.getHeaderName().getStringValue(), value.getStringValue());
                }

                decodeContext.setState(DecodeContext.STATE_HEADER_LINE_END);
            }
            // header line结束
            case DecodeContext.STATE_HEADER_LINE_END: {
                if (byteBuffer.remaining() < 3) {
                    break;
                }
                if (byteBuffer.get() != FeatUtils.LF) {
                    throw new HttpException(HttpStatus.BAD_REQUEST);
                }
                decodeContext.setState(DecodeContext.STATE_HEADER_END_CHECK);
                return decode(byteBuffer, request);
            }
            case DecodeContext.STATE_HEADER_IGNORE: {
                while (byteBuffer.remaining() >= 4) {
                    int position = byteBuffer.position() + 3;
                    byte b = byteBuffer.get(position);
                    if (b == FeatUtils.CR) {
                        byteBuffer.position(position - 2);
                        continue;
                    } else if (b != FeatUtils.LF) {
                        byteBuffer.position(position);
                        continue;
                    }
                    // header 结束符匹配，最后2字节已经是CR、LF,无需重复验证
                    if (byteBuffer.getShort(position - 3) == 3338) {
                        byteBuffer.position(position + 1);
                        decodeContext.setState(DecodeContext.STATE_HEADER_CALLBACK);
                        return true;
                    } else {
                        byteBuffer.position(position - 1);
                    }
                }
                return false;
            }
            case DecodeContext.STATE_BODY_READING_MONITOR:
                decodeContext.setState(DecodeContext.STATE_BODY_READING_CALLBACK);
                if (byteBuffer.position() > 0) {
                    break;
                }
            case DecodeContext.STATE_BODY_READING_CALLBACK:
                return byteBuffer.hasRemaining();
        }
        return false;
    }
}

