import java.io.File;
import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            // Récupérer l'objet distant (stub) du service de l'établissement en utilisant Naming.lookup()
            //MinistereService etablissementService = (MinistereService) Naming.lookup("//localhost/EtablissementService");
            Registry registry = LocateRegistry.getRegistry("localhost", 50010);

            // Récupérer l'objet distant (stub) du service de l'établissement
            MinistereService etablissementService = (MinistereService) registry.lookup("EtablissementService");

            // Lecture du fichier et conversion en tableau d'octets
            File file = new File("Data_sets (1).xlsx");
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileData);
            fis.close();

            // Envoi du fichier au serveur
            etablissementService.sendFile(fileData, "Data_sets");

            // Exemple de demande de prédiction
            String studentID = "Étudiant1";
            double[] marks = {10.5, 12.0, 11.8};
            String prediction = etablissementService.requestPrediction(studentID, marks);
            System.out.println("Prédiction pour l'étudiant " + studentID + " : " + prediction);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
