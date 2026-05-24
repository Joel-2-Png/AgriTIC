package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PompeClient {

    public static void main(String[] args) {

        String serveur = "localhost";
        int port = 6000;

        try {

            Socket socket = new Socket(serveur, port);

            System.out.println("Pompe connectée au serveur");

            BufferedReader lecture = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            String commande;

            while ((commande = lecture.readLine()) != null) {

                System.out.println("Commande reçue : " + commande);

                if (commande.startsWith("IRRIGUER")) {

                    String[] data = commande.split(";");

                    System.out.println(
                            "💧 Pompe activée pour la parcelle "
                            + data[1]
                    );
                }

                else if (commande.startsWith("STOP")) {

                    String[] data = commande.split(";");

                    System.out.println(
                            "⛔ Irrigation arrêtée pour "
                            + data[1]
                    );
                }
            }

        } catch (IOException e) {

            System.out.println("Erreur pompe : " + e.getMessage());
        }
    }
}