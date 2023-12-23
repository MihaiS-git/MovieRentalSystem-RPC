package org.example.movierentals.server.tcp;

import org.example.movierentals.common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.UnaryOperator;

public class TcpServer {
    private ExecutorService executorService;
    private Map<String, UnaryOperator<Message>> messageHandlers;

    public TcpServer(ExecutorService executorService) {
        this.executorService = executorService;
        this.messageHandlers = new HashMap<>();
    }

    public void addMessageHandler(String methodName, UnaryOperator<Message> handler) {
        messageHandlers.put(methodName, handler);
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");
                executorService.submit(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ClientHandler implements Runnable {

        public Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (var is = clientSocket.getInputStream();
                 var os = clientSocket.getOutputStream()
            ) {
                Message request = new Message();
                request.readFrom(is);

                Message response = messageHandlers.get(request.getHeader()).apply(request);
                response.writeTo(os);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}