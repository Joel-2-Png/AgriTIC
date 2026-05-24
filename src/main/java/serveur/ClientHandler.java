package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import modele.Parcelle;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {

            BufferedReader lecteur = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String message;

            while ((message = lecteur.readLine()) != null) {

                System.out.println("Message reçu : " + message);

                // Exemple :
                // PARCELLE;P01;Tomate;25;32

                String[] data = message.split(";");

                if (data[0].equals("PARCELLE")) {

                    String id = data[1];
                    String culture = data[2];

                    double humidite = Double.parseDouble(data[3]);
                    double temperature = Double.parseDouble(data[4]);

                    Parcelle parcelle = new Parcelle(
                            id,
                            culture,
                            humidite,
                            temperature
                    );

                    // Synchronisation simple
                    synchronized (ServeurAgriTIC.parcelles) {

                        ServeurAgriTIC.parcelles.put(id, parcelle);
                    }

                    System.out.println(parcelle);

                    // Analyse irrigation
                    if (humidite < 30) {

                        System.out.println("⚠ Parcelle sèche : " + id);

                    }

                    if (humidite < 20) {

                        System.out.println("🚨 Parcelle très sèche : " + id);
                    }

                    if (humidite >= 70) {

                        System.out.println("✅ Arrêt irrigation : " + id);
                    }
                }
            }

        } catch (IOException e) {

            System.out.println("Client déconnecté.");
        }
    }
}