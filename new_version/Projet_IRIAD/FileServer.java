import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FileServer extends UnicastRemoteObject implements FileServerInterface {
    private static final long serialVersionUID = 1L;

    protected FileServer() throws RemoteException {
        super();
    }

    @Override
    public byte[] getFile() throws RemoteException {
        byte[] data = null;
        try {
            // Read the file
            File file = new File("example.xlsx");
            FileInputStream fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            
            // Execute Python script
            ProcessBuilder pb = new ProcessBuilder("python", "script.py");
            pb.directory(new File("C:\\2SID\\Projet_IRIAD"));
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = p.waitFor();
            System.out.println("Python script exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        try {
            FileServer server = new FileServer();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("//localhost/FileServer", server);
            System.out.println("Server started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
