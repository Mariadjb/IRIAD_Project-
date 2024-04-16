import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends UnicastRemoteObject implements Client {

    protected ClientImpl() throws RemoteException {
        super();
    }

    @Override
    public void receiveProcessedFile(String processedData) throws RemoteException {
        // Print processed data received from server
        System.out.println("Processed data received: " + processedData);
    }

    public void sendPredictionFile(String filePath, String fileName, String clientId) throws RemoteException {
        sendFile(filePath, fileName, clientId, "prediction");
    }

    public void sendTrainingFile(String filePath, String fileName, String clientId) throws RemoteException {
        sendFile(filePath, fileName, clientId, "training");
    }

    private void sendFile(String filePath, String fileName, String clientId, String sendingType) {
        try {
            File file = new File(filePath);
            byte[] fileData = Files.readAllBytes(file.toPath());

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Server server = (Server) registry.lookup("Server");
            server.receiveFile(fileData, fileName, clientId, sendingType);
            System.out.println("File sent: " + fileName + " with type: " + sendingType);
        } catch (Exception e) {
            System.err.println("Error sending file: " + e.toString());
            e.printStackTrace();
        }
    }
    public void receiveFile(byte[] fileData, String fileName, String clientId, String sendingType) throws RemoteException {
        try {
            // Determine the folder based on the sending type
            String folderName =  clientId + "_files";

            // Create the folder if it doesn't exist
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Write the received file to the folder
            File file = new File(folder, fileName);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(fileData);
                System.out.println("File received from server: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error receiving file: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                System.err.println("Usage: java ClientImpl <file_path> <file_path> <client_id>");
                System.exit(1);
            }
            String filePath1 = args[0];
            String filePath2 = args[1];
            String clientId = args[2];
            String fileName1 = new File(filePath1).getName();
            String fileName2 = new File(filePath2).getName();
            Client client = new ClientImpl();
            // Locate the RMI registry running on localhost at port 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
           // Bind the client object to the registry with a unique name
           registry.bind("Client" + clientId, client);
            // Send prediction file
            client.sendPredictionFile(filePath2, fileName2, clientId);
            System.out.println("Prediction file sent.");

            // Send training file
            client.sendTrainingFile(filePath1, fileName1, clientId);
            System.out.println("Training file sent.");

            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
