import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MinistereService extends Remote {
    void sendFile(byte[] fileData, String fileName) throws RemoteException;
    String requestPrediction(String studentID, double[] marks) throws RemoteException;
    byte[] predict(byte[] fileData, String fileName) throws RemoteException;
}
