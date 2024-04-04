import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;


public class JavaRunCommandpredict {

    public static void main(String args[]) {

        String s = null;

        try {
            // Run the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python", "predict_bac.py");
            processBuilder.directory(new File("C:\\Users\\KM-USER\\Documents\\iriad_projet"));
            Process p = processBuilder.start();

            // Read the output of the Python script
            InputStream inputStream = p.getInputStream();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(inputStream));

            // Read the errors, if any, from the attempted command
            InputStream errorStream = p.getErrorStream();
            BufferedReader stdError = new BufferedReader(new InputStreamReader(errorStream));

            // Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read any errors from the attempted command
            System.out.println("\nHere is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            // Close streams
            stdInput.close();
            stdError.close();

            // Wait for the process to complete
            int exitCode = p.waitFor();
            System.out.println("Exited with error code " + exitCode);
        } catch (IOException e) {
            System.out.println("Exception happened - here's what I know: ");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted while waiting for the process to complete");
            e.printStackTrace();
        }
    }
}
