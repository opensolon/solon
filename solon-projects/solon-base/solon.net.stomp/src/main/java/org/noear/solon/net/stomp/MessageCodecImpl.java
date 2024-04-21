package org.noear.solon.net.stomp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 消息编解码器实现
 *
 * @author limliu
 * @since 2.7
 */
public class MessageCodecImpl implements MessageCodec {

    /**
     * Command 结束符
     */
    private String commandEnd = "\n";

    /**
     * Headers 结束符
     */
    private String headersEnd = "\n\n";

    /**
     * Header 之间的分隔符
     */
    private String headerDelimiter = "\n";

    /**
     * Header 键值 间的分隔符
     */
    private String headerKvDelimiter = ":";

    /**
     * Body 结束符
     */
    private String bodyEnd = "\u0000";

    /**
     * 待解析的 Stomp 报文容器
     */
    private final StringBuilder pending = new StringBuilder();

    @Override
    public String encode(Message input) {
        StringBuilder sb = new StringBuilder();
        sb.append(input.getCommand()).append(commandEnd);
        List<Header> headers = msgHeaders(input);
        int hCnt = headers.size();
        for (int index = 0; index < hCnt; index++) {
            Header header = headers.get(index);
            sb.append(header.getKey())
                    .append(headerKvDelimiter)
                    .append(header.getValue());
            if (index < hCnt - 1) {
                sb.append(headerDelimiter);
            }
        }
        sb.append(headersEnd);
        String payload = input.getPayload();
        if (payload != null) {
            sb.append(payload);
        }
        sb.append(bodyEnd);
        return sb.toString();
    }

    protected List<Header> msgHeaders(Message input) {
        List<Header> headers = input.getHeaders();
        return new ArrayList<>(headers);
    }

    @Override
    public synchronized void decode(String input, Consumer<Message> out) {
        if (input == null || input.isEmpty()) {
            return;
        }
        // 装入待解析容器
        pending.append(input);
        // 开始解析
        decode(out, 0);
    }

    /**
     * @param out   输出
     * @param start 开始解析的问题
     */
    protected void decode(Consumer<Message> out, int start) {
        cleanPendingStartData(start);
        // Body 结尾符下标
        int bEndIdx = pending.indexOf(bodyEnd);
        if (bEndIdx < 0) {
            // 数据包尚未接收完毕，直接返回
            return;
        }
        // Command 结尾符下标
        int cEndIdx = pending.indexOf(commandEnd);
        // Headers 结尾符下标
        int hEndIdx = pending.indexOf(headersEnd);
        if (cEndIdx <= 0 || hEndIdx <= cEndIdx || bEndIdx <= hEndIdx) {
            // 非法数据包，跳过一个字符重新解析
            this.decode(out, 1);
            return;
        }
        // 解析 Command
        String command = pending.substring(0, cEndIdx).trim();
        if (!isCommand(command)) {
            // 非法数据包，跳过一个字符重新解析
            this.decode(out, 1);
            return;
        }
        // 解析 Headers
        List<Header> headers = this.decodeHeaders(cEndIdx, hEndIdx);
        // 解析 Body
        String payload = pending.substring(hEndIdx + headersEnd.length(), bEndIdx);
        // 输出解析结果
        out.accept(createMessage(command, headers, payload));
        // 重新解析 bodyEnd 之后的数据
        this.decode(out, bEndIdx + bodyEnd.length());
    }

    protected void cleanPendingStartData(int start) {
        if (start > 0) {
            // 清空 start 之前的数据
            pending.delete(0, start);
        }
        // Command 开始符下标
        int index = 0;
        for (int i = 0; i < pending.length(); i++) {
            char c = pending.charAt(i);
            if (this.isCommandChar(c)) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            // 清空 Command 之前的数据
            pending.delete(0, index);
        }
    }

    /**
     * 解析 Headers
     *
     * @param cEndIdx Command 结尾符下标
     * @param hEndIdx Headers 结尾符下标
     * @return Headers
     */
    protected List<Header> decodeHeaders(int cEndIdx, int hEndIdx) {
        String[] strHeaders = pending.substring(cEndIdx + commandEnd.length(), hEndIdx)
                .split(headerDelimiter);
        List<Header> headers = new ArrayList<>(strHeaders.length);
        for (String header : strHeaders) {
            if (header == null) {
                continue;
            }
            int start = header.indexOf(headerKvDelimiter);
            if (start < 1) {
                continue;
            }
            headers.add(new Header(header.substring(0, start), header.substring(start + headerKvDelimiter.length(), header.length())));
        }
        return headers;
    }

    protected boolean isCommand(String command) {
        return !command.isEmpty() && command.matches("[A-Z]+");
    }

    protected boolean isCommandChar(char c) {
        return c >= 'A' && c <= 'Z';
    }

    protected Message createMessage(String command, List<Header> headers, String payload) {
        return new Message(command, headers, payload);
    }

    public String getCommandEnd() {
        return commandEnd;
    }

    public void setCommandEnd(String commandEnd) {
        if (commandEnd != null) {
            this.commandEnd = commandEnd;
        }
    }

    public String getHeadersEnd() {
        return headersEnd;
    }

    public void setHeadersEnd(String headersEnd) {
        if (headersEnd != null) {
            this.headersEnd = headersEnd;
        }
    }

    public String getBodyEnd() {
        return bodyEnd;
    }

    public void setBodyEnd(String bodyEnd) {
        if (bodyEnd != null) {
            this.bodyEnd = bodyEnd;
        }
    }

    public String getHeaderDelimiter() {
        return headerDelimiter;
    }

    public void setHeaderDelimiter(String headerDelimiter) {
        if (headerDelimiter != null) {
            this.headerDelimiter = headerDelimiter;
        }
    }

    public String getHeaderKvDelimiter() {
        return headerKvDelimiter;
    }

    public void setHeaderKvDelimiter(String headerKvDelimiter) {
        if (headerKvDelimiter != null) {
            this.headerKvDelimiter = headerKvDelimiter;
        }
    }
}