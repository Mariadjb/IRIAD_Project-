import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private static final String LAST_FILES_FOLDER = "last_files";
    private static final String FILE_TYPE_TO_SEND = "result_file";
    private AtomicInteger predictionFilesReceived = new AtomicInteger(0);
    private AtomicInteger trainingFilesReceived = new AtomicInteger(0);
    private static final String PREDICTION_FOLDER = "prediction_files";
    private static final String TRAINING_FOLDER = "training_files";
    private Set<String> clientsWithTrainingFiles = new HashSet<>();

    protected ServerImpl() throws RemoteException {
        super();
    }

   
    
   @Override
   public synchronized void receiveFile(byte[] fileData, String fileName, String clientId, String sendingType) throws RemoteException {
    try {
        // Determine the folder based on the sending type
        String folderName = sendingType.equals("prediction") ? PREDICTION_FOLDER : TRAINING_FOLDER;

        // Create the folder if it doesn't exist
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Write the received file to the folder
        File file = new File(folder, clientId + "_" + fileName); // Include client ID in the filename
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(fileData);
            System.out.println("File received: " + fileName + " from client: " + clientId + " with type: " + sendingType);

            if (sendingType.equals("prediction")) {
                predictionFilesReceived.incrementAndGet();

                // Check if "association_rule.pkl" file exists in the last_files folder
                File lastFile = new File("association_rule.pkl");
                if (lastFile.exists()) {
                    System.out.println("prediction_files/" + clientId + "_" + fileName);
                    executePredictionCode("./prediction_files/" + clientId + "_" + fileName); // Execute prediction script
                    sendFileToClient(clientId);
                } else {
                    // Send a message to the client to wait for two training files
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    try {
                        Client client = (Client) registry.lookup("Client" + clientId);
                        client.receiveProcessedFile("Please wait for two training files.");
                    } catch (NotBoundException e) {
                        System.err.println("Error: Client with ID " + clientId + " not found in the registry.");
                    }
                }
            } else if (sendingType.equals("training")) {
                // Check if this client has not already sent a training file
                if (!clientsWithTrainingFiles.contains(clientId)) {
                    trainingFilesReceived.incrementAndGet();
                    clientsWithTrainingFiles.add(clientId); // Add client ID to the set

                    // Check if all files have been received
                    if (trainingFilesReceived.get() == 2) {
                        executeTrainingCode(); // Execute Python code
                        
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error receiving file: " + e.getMessage());
    }
}

    
private void executeTrainingCode() {
    try {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "model.py");
        processBuilder.directory(Paths.get("").toAbsolutePath().toFile()); // Set the working directory to the current directory
        Process process = processBuilder.start();
        
        // Capture standard output
        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = stdoutReader.readLine()) != null) {
            System.out.println("Python stdout: " + line);
        }
        
        // Capture standard error
        BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = stderrReader.readLine()) != null) {
            System.err.println("Python stderr: " + line);
        }
        
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Python code executed successfully.");
        } else {
            System.err.println("Error executing Python code. Exit code: " + exitCode);
        }
    } catch (IOException | InterruptedException e) {
        System.err.println("Error executing Python code: " + e.getMessage());
    }
}
private void executePredictionCode(String fileName) {
    try {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "predect.py", fileName);
        processBuilder.directory(Paths.get("").toAbsolutePath().toFile()); // Set the working directory to the current directory
        Process process = processBuilder.start();
        
        // Capture standard output
        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = stdoutReader.readLine()) != null) {
            System.out.println("Python stdout: " + line);
        }
        
        // Capture standard error
        BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = stderrReader.readLine()) != null) {
            System.err.println("Python stderr: " + line);
        }
        
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Python code executed successfully.");
        } else {
            System.err.println("Error executing Python code. Exit code: " + exitCode);
        }
    } catch (IOException | InterruptedException e) {
        System.err.println("Error executing Python code: " + e.getMessage());
    }
}


private void sendFileToClient(String clientId) {
    try {
        // Get the list of files in the last_files folder
        File lastFilesFolder = new File(LAST_FILES_FOLDER);
        File[] files = lastFilesFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith(clientId +"_"); // Filter files by extension and client ID
            }
        });

        // Check if any files exist
        if (files != null && files.length > 0) {
            // Get the file with the client ID in its name
            File clientFile = files[0]; // Assuming there's only one file per client ID
            byte[] fileData = new byte[(int) clientFile.length()];
            try (FileInputStream fileInputStream = new FileInputStream(clientFile)) {
                fileInputStream.read(fileData);
            }

            // Send the file to the client
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Client client = (Client) registry.lookup("Client" + clientId);
            client.receiveFile(fileData, clientFile.getName(), clientId, FILE_TYPE_TO_SEND);
            System.out.println("File sent to client: " + clientFile.getName());
        } else {
            System.out.println("No files found for client: " + clientId);
        }
    } catch (Exception e) {
        System.err.println("Error sending file to client: " + e.getMessage());
    }
}


    public static void main(String[] args) {
        try {
            ServerImpl server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("Server", server);
            System.out.println("Server is running...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
