package console;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import rmi.AgriTICRemote;

public class ConsoleAgriculteurRMI {

    public static void main(String[] args) {

        try {

            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            AgriTICRemote service =
                    (AgriTICRemote) registry.lookup(
                            "AgriTICService"
                    );

            System.out.println("=== LISTE DES PARCELLES ===");

            List<String> parcelles =
                    service.getListeParcelles();

            for (String p : parcelles) {

                System.out.println(p);
            }

            System.out.println("\n=== PARCELLES SÈCHES ===");

            List<String> seches =
                    service.getParcellesSeches();

            for (String p : seches) {

                System.out.println(p);
            }

            System.out.println("\n=== ETAT POMPE ===");

            System.out.println(service.getEtatPompe());

            // Irrigation manuelle
            service.irriguerManuellement("P01");

            System.out.println(
                    "\n✅ Irrigation manuelle envoyée"
            );

        } catch (Exception e) {

            System.out.println(
                    "Erreur RMI : " + e.getMessage()
            );
        }
    }
}
