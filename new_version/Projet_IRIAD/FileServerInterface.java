
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface FileServerInterface extends Remote {
    byte[] getFile() throws RemoteException;
}