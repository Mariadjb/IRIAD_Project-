
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MinistereService extends Remote {
    //void recevoirResultatsEleves(byte[] fichierResultats) throws RemoteException;
    //void determinerReglesAssociation() throws RemoteException;
    //void entrainerModelesPrediction() throws RemoteException;
    //double predireResultatEleve(int idEleve, double[] notesTrimestres) throws RemoteException;
    
    void sendFile(byte[] fileData, String fileName) throws RemoteException;
    String requestPrediction(String studentID, double[] marks) throws RemoteException;
}

