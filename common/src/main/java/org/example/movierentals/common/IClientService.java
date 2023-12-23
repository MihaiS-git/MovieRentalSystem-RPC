package org.example.movierentals.common;


import org.example.movierentals.common.domain.Client;

import java.util.concurrent.Future;

public interface IClientService {

    Future<String> getAllClients();

    Future<String> addClient(Client client);

    Future<String> getClientById(Long id);

    Future<String> updateClient(Client client);

    Future<String> deleteClientById(Long id);

    Future<String> filterClientsByKeyword(String keyword);

}

