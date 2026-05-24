package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class ParcelleClient {

    public static void main(String[] args) {

        String serveur = "localhost";
        int port = 5000;

        try {

            Socket socket = new Socket(serveur, port);

            System.out.println("Connecté au serveur AgriTIC");

            PrintWriter ecriture = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            Random random = new Random();

            while (true) {

                // Génération des données
                String id = "P01";
                String culture = "Tomate";

                int humidite = random.nextInt(100);
                int temperature = 20 + random.nextInt(15);

                String message = "PARCELLE;"
                        + id + ";"
                        + culture + ";"
                        + humidite + ";"
                        + temperature;

                // Envoi
                ecriture.println(message);

                System.out.println("Message envoyé : " + message);

                // Attendre 5 secondes
                Thread.sleep(5000);
            }

        } catch (IOException | InterruptedException e) {

            System.out.println("Erreur : " + e.getMessage());
        }
    }
}