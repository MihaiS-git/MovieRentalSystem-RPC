package org.example.movierentals.client.tcp;

import org.example.movierentals.common.Message;

import java.io.IOException;
import java.net.Socket;

public class TcpClient {
    public Message sendAndReceive(Message request) {
        try (Socket socket = new Socket("localhost", 1234);
             var is = socket.getInputStream();
             var os = socket.getOutputStream();) {

            request.writeTo(os);

            Message response = new Message();
            response.readFrom(is);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
