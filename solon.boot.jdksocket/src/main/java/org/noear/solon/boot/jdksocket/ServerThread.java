package org.noear.solon.boot.jdksocket;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket = null;
    private InputStream inputStream = null;
    private InputStreamReader inputStreamReader = null;
    private BufferedReader bufferedReader = null;
    private OutputStream outputStream = null;
    private PrintStream printStream = null;
    private String a;
    private String b = "地瓜地瓜,我是土豆";

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        try {
            while((a = bufferedReader.readLine()) != null){
                System.out.println("客户端说:"+a);
                socket.shutdownInput();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printStream = new PrintStream(outputStream);
        try {
            printStream.write(b.getBytes());
            socket.shutdownOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
