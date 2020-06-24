package webapp;


import java.io.OutputStream;

public class HttpApp {
    public static void main(String[] args) throws Exception {
        net.freeutils.httpserver.HTTPServer _server = new net.freeutils.httpserver.HTTPServer(8080);

        net.freeutils.httpserver.HTTPServer.VirtualHost host = _server.getVirtualHost((String) null);

        host.setDirectoryIndex(null);
        host.addContext("/", (req, resp) -> {
            String text = "Hello world!";

            byte[] content = text.getBytes("UTF-8");
            resp.sendHeaders(200, -1, -1,
                    "W/\"" + Integer.toHexString(text.hashCode()) + "\"",
                    "text/html; charset=utf-8", null);

            resp.getBody().write(content);
            resp.getBody().write(content);


            return 0;
        });

        _server.start();

    }
}
