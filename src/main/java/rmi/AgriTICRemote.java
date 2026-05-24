package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AgriTICRemote extends Remote {

    List<String> getListeParcelles()
            throws RemoteException;

    List<String> getParcellesSeches()
            throws RemoteException;

    String getEtatPompe()
            throws RemoteException;

    void irriguerManuellement(String idParcelle)
            throws RemoteException;
}