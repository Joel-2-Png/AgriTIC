package serveur;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import modele.Parcelle;

public class ServeurAgriTIC {

    public static Map<String, Parcelle> parcelles = new HashMap<>();

    // Communication avec la pompe
    public static PrintWriter pompeWriter;

    public static void main(String[] args) {

        int portParcelle = 5000;
        int portPompe = 6000;

        try {

            // Serveur parcelles
            ServerSocket serveurParcelle =
                    new ServerSocket(portParcelle);

            // Serveur pompe
            ServerSocket serveurPompe =
                    new ServerSocket(portPompe);

            System.out.println("Serveur AgriTIC démarré");

            // Thread pompe
            new Thread(() -> {

                try {

                    System.out.println("En attente de la pompe...");

                    Socket pompeSocket = serveurPompe.accept();

                    pompeWriter = new PrintWriter(
                            pompeSocket.getOutputStream(),
                            true
                    );

                    System.out.println("✅ Pompe connectée");

                } catch (IOException e) {

                    System.out.println("Erreur pompe");
                }

            }).start();

            // Gestion des parcelles
            while (true) {

                System.out.println(
                        "En attente d'une parcelle..."
                );

                Socket socket = serveurParcelle.accept();

                System.out.println(
                        "Nouvelle parcelle connectée"
                );

                ClientHandler clientHandler =
                        new ClientHandler(socket);

                Thread thread =
                        new Thread(clientHandler);

                thread.start();
            }

        } catch (IOException e) {

            System.out.println(
                    "Erreur serveur : " + e.getMessage()
            );
        }
    }
}