import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            // Retrieve the remote object (stub) of the Ministere service
            Registry registry = LocateRegistry.getRegistry("localhost", 50010);
            MinistereService etablissementService = (MinistereService) registry.lookup("EtablissementService");

            // Read the file and convert to byte array
            File file = new File("Data_sets (1).xlsx");
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();
 // Send the file to the server and receive the modified file with predictions
 byte[] receivedFileData = etablissementService.predict(fileData, "Data_sets (1).xlsx");

 // Save the modified file
 FileOutputStream fos = new FileOutputStream("Modified_Data_sets (1).xlsx");
 fos.write(receivedFileData);
 fos.close();

 System.out.println("Received file with predictions saved as Modified_Data_sets (1).xlsx.");

} catch (Exception e) {
 e.printStackTrace();
 }
}
}
