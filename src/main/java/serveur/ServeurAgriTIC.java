package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import modele.Parcelle;

public class ServeurAgriTIC {

    // Collection partagée des parcelles
    public static Map<String, Parcelle> parcelles = new HashMap<>();

    public static void main(String[] args) {

        int port = 5000;

        try {

            ServerSocket serveur = new ServerSocket(port);

            System.out.println("Serveur AgriTIC démarré sur le port " + port);

            while (true) {

                System.out.println("En attente de connexion...");

                Socket socket = serveur.accept();

                System.out.println("Nouvelle connexion : "
                        + socket.getInetAddress());

                // Création du thread
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);

                thread.start();
            }

        } catch (IOException e) {

            System.out.println("Erreur serveur : " + e.getMessage());
        }
    }
}
