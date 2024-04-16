import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void receiveFile(byte[] fileData, String fileName, String clientId, String sendingType) throws RemoteException ;
    //void processFiles() throws RemoteException;
}
