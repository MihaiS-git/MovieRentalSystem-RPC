package org.example.movierentals.client;

import org.example.movierentals.client.service.CClientServiceImpl;
import org.example.movierentals.client.service.CMovieServiceImpl;
import org.example.movierentals.client.service.CRentalServiceImpl;
import org.example.movierentals.client.tcp.TcpClient;
import org.example.movierentals.client.ui.Console;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client Main App
 */
public class ClientApp {
    public static void main(String[] args) {
        System.out.println("Client is running...");
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        TcpClient tcpClient = new TcpClient();

        CMovieServiceImpl movieService = new CMovieServiceImpl(executorService, tcpClient);
        CClientServiceImpl clientService = new CClientServiceImpl(executorService, tcpClient);
        CRentalServiceImpl rentalService = new CRentalServiceImpl(executorService, tcpClient);

        Console console = new Console(movieService, clientService, rentalService);

        console.runConsole();

        executorService.shutdown();
        System.out.println("Client shut down.");
    }
}