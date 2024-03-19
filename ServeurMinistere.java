import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServeurMinistere {
    public static void main(String[] args) {
        try {
            // Créer un registre RMI sur le port 1099
            Registry registry = LocateRegistry.createRegistry(50010);

            // Créer une instance du serveur Ministere et la lier au registre
            MinistereService ministereService = new MinistereServiceImpl();
            registry.rebind("EtablissementService", ministereService);

            System.out.println("Serveur Ministère démarré.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
