package org.example.movierentals.client.service;

import org.example.movierentals.client.tcp.TcpClient;
import org.example.movierentals.common.IClientService;
import org.example.movierentals.common.Message;
import org.example.movierentals.common.domain.Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CClientServiceImpl implements IClientService {
    ExecutorService executorService;
    TcpClient tcpClient;

    public CClientServiceImpl(ExecutorService executorService, TcpClient tcpClient) {
        this.executorService = executorService;
        this.tcpClient = tcpClient;
    }

    @Override
    public Future<String> getAllClients() {
        return executorService.submit(() -> {
            Message request = new Message("getAllClients");
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> addClient(Client client) {
        StringBuilder sb = new StringBuilder();
        sb.append(client.getFirstName()).append(",")
                .append(client.getLastName()).append(",")
                .append(client.getDateOfBirth()).append(",")
                .append(client.getEmail()).append(",")
                .append(client.isSubscribe()).append(",");
        return executorService.submit(() ->{
            Message request = new Message("addClient", sb.toString());
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> getClientById(Long id) {
        return executorService.submit(() -> {
            Message request = new Message("getClientById", String.valueOf(id));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> updateClient(Client client) {
        StringBuilder sb = new StringBuilder();
        sb.append(client.getId()).append(",")
                .append(client.getFirstName()).append(",")
                .append(client.getLastName()).append(",")
                .append(client.getDateOfBirth()).append(",")
                .append(client.getEmail()).append(",")
                .append(client.isSubscribe());

        return executorService.submit(() -> {
            Message request = new Message("updateClient", sb.toString());
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> deleteClientById(Long id) {
        return executorService.submit(() -> {
            Message request = new Message("deleteClientById", String.valueOf(id));
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

    @Override
    public Future<String> filterClientsByKeyword(String keyword) {
        return executorService.submit(() -> {
            Message request = new Message("filterClientsByKeyword", keyword);
            return tcpClient.sendAndReceive(request).getBody();
        });
    }

}
