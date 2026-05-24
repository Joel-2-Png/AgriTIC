package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class ParcelleClient {

    public static void main(String[] args) {

        String serveur = "localhost";
        int port = 5000;

        // 🌿 5 PARCELLES FIXES
        String[][] parcelles = {
                {"P01", "Tomate"},
                {"P02", "Oignon"},
                {"P03", "Salade"},
                {"P04", "Igname"},
                {"P05", "Piment"}
        };

        try {

            Socket socket = new Socket(serveur, port);

            System.out.println("🌿 Connecté au serveur AgriTIC");

            PrintWriter ecriture = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            Random random = new Random();

            while (true) {

                for (String[] parcelle : parcelles) {

                    String id = parcelle[0];
                    String culture = parcelle[1];

                    // 🌡 humidité ALÉATOIRE POUR CHAQUE PARCELLE
                    int humidite = 10 + random.nextInt(91); // 10 à 100
                    int temperature = 18 + random.nextInt(17); // 18 à 35

                    String message = "PARCELLE;"
                            + id + ";"
                            + culture + ";"
                            + humidite + ";"
                            + temperature;

                    ecriture.println(message);

                    System.out.println("📤 " + id +
                            " | " + culture +
                            " → 💧 " + humidite +
                            "% | 🌡 " + temperature + "°C");
                }

                // ⏱ pause entre cycles
                Thread.sleep(5000);
            }

        } catch (IOException | InterruptedException e) {

            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }
}