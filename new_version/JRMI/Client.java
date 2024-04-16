import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
  
   void receiveProcessedFile(String processedData) throws RemoteException ;
   public void sendTrainingFile(String filePath, String fileName, String clientId) throws RemoteException;
   public void sendPredictionFile(String filePath, String fileName, String clientId) throws RemoteException;
   void receiveFile(byte[] fileData, String fileName, String clientId, String sendingType) throws RemoteException;
}
