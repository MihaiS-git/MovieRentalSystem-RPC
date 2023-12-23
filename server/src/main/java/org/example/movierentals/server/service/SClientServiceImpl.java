package org.example.movierentals.server.service;

import org.example.movierentals.common.IClientService;
import org.example.movierentals.common.domain.Client;
import org.example.movierentals.server.repository.ClientDBRepository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SClientServiceImpl implements IClientService {
    private ClientDBRepository clientRepository;
    private ExecutorService executorService;

    public SClientServiceImpl(ExecutorService executorService, ClientDBRepository clientRepository) {
        this.executorService = executorService;
        this.clientRepository = clientRepository;
    }

    @Override
    public Future<String> getAllClients() {
        Iterable<Client> clients = clientRepository.findAll();
        if (StreamSupport.stream(clients.spliterator(), false).findAny().isPresent()) {
            StringBuilder sb = new StringBuilder();
            for (Client client : clients) {
                sb.append(client.getId()).append(",")
                        .append(client.getFirstName()).append(",")
                        .append(client.getLastName()).append(",")
                        .append(client.getDateOfBirth()).append(",")
                        .append(client.getEmail()).append(",")
                        .append(client.isSubscribe()).append(";");
            }
            return executorService.submit(() -> sb.toString());
        } else {
            return executorService.submit(() -> "400 Error,No Clients were found in the Database");
        }
    }

    @Override
    public Future<String> addClient(Client client) {
        Optional<Client> savedClient = clientRepository.save(client);
        if (savedClient.isPresent()) {
            Client responseClient = savedClient.get();
            StringBuilder sb = new StringBuilder();
            sb.append(responseClient.getFirstName()).append(",")
                    .append(responseClient.getLastName()).append(",")
                    .append(responseClient.getDateOfBirth()).append(",")
                    .append(responseClient.getEmail()).append(",")
                    .append(responseClient.isSubscribe());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Client not saved.");
    }

    @Override
    public Future<String> getClientById(Long id) {
        Optional<Client> clientOptional = clientRepository.findOne(id);
        StringBuilder sb = new StringBuilder();
        if (clientOptional.isPresent()) {
            Client responseClient = clientOptional.get();
            sb.append(responseClient.getId()).append(",")
                    .append(responseClient.getFirstName()).append(",")
                    .append(responseClient.getLastName()).append(",")
                    .append(responseClient.getDateOfBirth()).append(",")
                    .append(responseClient.getEmail()).append(",")
                    .append(responseClient.isSubscribe());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Client not found.");
    }

    @Override
    public Future<String> updateClient(Client client) {
        Optional<Client> clientOptional = clientRepository.update(client);
        StringBuilder sb = new StringBuilder();
        if (clientOptional.isPresent()) {
            Client responseClient = clientOptional.get();
            sb.append(responseClient.getId()).append(",")
                    .append(responseClient.getFirstName()).append(",")
                    .append(responseClient.getLastName()).append(",")
                    .append(responseClient.getDateOfBirth()).append(",")
                    .append(responseClient.getEmail()).append(",")
                    .append(responseClient.isSubscribe());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Client not found for update.");
    }

    @Override
    public Future<String> deleteClientById(Long id) {
        Optional<Client> clientOptional = clientRepository.delete(id);
        StringBuilder sb = new StringBuilder();
        if (clientOptional.isPresent()) {
            Client deletedClient = clientOptional.get();
            sb.append(deletedClient.getId()).append(",")
                    .append(deletedClient.getFirstName()).append(",")
                    .append(deletedClient.getLastName()).append(",")
                    .append(deletedClient.getDateOfBirth()).append(",")
                    .append(deletedClient.getEmail()).append(",")
                    .append(deletedClient.isSubscribe());
            return executorService.submit(() -> sb.toString());
        }
        return executorService.submit(() -> "400 Error,Client not found to delete.");
    }

    @Override
    public Future<String> filterClientsByKeyword(String keyword) {
        Iterable<Client> clientsSet = clientRepository.findAll();
        StringBuilder sb = new StringBuilder();
        if (StreamSupport.stream(clientsSet.spliterator(), false).findAny().isPresent()) {
            Set<Client> filteredClients = StreamSupport.stream(clientsSet.spliterator(), false)
                    .filter(c -> c.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                            c.getLastName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toSet());
            if(filteredClients.size() > 0) {
                filteredClients.forEach(c -> {
                    sb.append(c.getId()).append(",")
                            .append(c.getFirstName()).append(",")
                            .append(c.getLastName()).append(",")
                            .append(c.getDateOfBirth()).append(",")
                            .append(c.getEmail()).append(",")
                            .append(c.isSubscribe()).append(";");
                });
                return executorService.submit(() -> sb.toString());
            } else {
                return executorService.submit(() -> "400 Error,No movie found for the Keyword");
            }
        } else {
            return executorService.submit(() -> "400 Error,No clients found containing the keyword.");
        }
    }
}
