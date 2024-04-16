import java.io.FileOutputStream;
import java.rmi.Naming;

public class FileClient {
    public static void main(String[] args) {
        try {
            FileServerInterface server = (FileServerInterface) Naming.lookup("//localhost/FileServer");
            byte[] data = server.getFile();
            FileOutputStream fos = new FileOutputStream("received.xlsx");
            fos.write(data);
            fos.close();
            System.out.println("File received successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
