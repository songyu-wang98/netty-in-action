package nia.chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kerr.
 * <p>
 * Listing 1.1 Blocking I/O example
 */
public class BlockingIoExample {

    /**
     * Listing 1.1 Blocking I/O example
     */
    public void serve(int portNumber) throws IOException {
        Socket clientSocket;
        // 改成 try-with-resources 语法
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { // 监听指定端口上的连接请求
            clientSocket = serverSocket.accept(); // 直到一个连接建立前，都会阻塞
            BufferedReader in = new BufferedReader( // BufferedReader 衍生自 Socket 的输入流，从一个字符输入流中读取文本
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter( // PrintWriter 衍生自 Socket 的输出流，向一个字符输出流打印对象的格式化表示
                    clientSocket.getOutputStream(), true);
            String request, response;
            while ((request = in.readLine()) != null) { // readLine() 也会阻塞，直到读取到换行符或者回车符
                if ("Done".equals(request)) {
                    break;
                }
                response = processRequest(request);
                out.println(response);
            }
        }
    }

    private String processRequest(String request) {
        return "Processed";
    }
}
