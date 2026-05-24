package serveur;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import modele.Parcelle;
import rmi.AgriTICRemote;

public class AgriTICRemoteImpl
        extends UnicastRemoteObject
        implements AgriTICRemote {

    protected AgriTICRemoteImpl()
            throws RemoteException {

        super();
    }

    @Override
    public List<String> getListeParcelles()
            throws RemoteException {

        List<String> liste = new ArrayList<>();

        synchronized (ServeurAgriTIC.parcelles) {

            for (Parcelle p : ServeurAgriTIC.parcelles.values()) {

                liste.add(p.toString());
            }
        }

        return liste;
    }

    @Override
    public List<String> getParcellesSeches()
            throws RemoteException {

        List<String> liste = new ArrayList<>();

        synchronized (ServeurAgriTIC.parcelles) {

            for (Parcelle p : ServeurAgriTIC.parcelles.values()) {

                if (p.getHumidite() < 30) {

                    liste.add(p.toString());
                }
            }
        }

        return liste;
    }

    @Override
    public String getEtatPompe()
            throws RemoteException {

        if (ServeurAgriTIC.pompeWriter != null) {

            return "Pompe connectée";
        }

        return "Pompe déconnectée";
    }

    @Override
    public void irriguerManuellement(String idParcelle)
            throws RemoteException {

        if (ServeurAgriTIC.pompeWriter != null) {

            ServeurAgriTIC.pompeWriter.println(
                    "IRRIGUER;" + idParcelle
            );

            System.out.println(
                    "Irrigation manuelle envoyée pour "
                    + idParcelle
            );
        }
    }
}