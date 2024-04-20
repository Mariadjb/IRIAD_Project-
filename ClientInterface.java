import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class ClientInterface extends JFrame {

    private JTextField filePathField;
    private JComboBox<String> sendTypeComboBox;
    private JTextField clientIdField;
    private JTextArea fileContentTextArea;

    public ClientInterface() {
        setTitle("Client Interface");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 1));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filePathLabel = new JLabel("File Path:");
        filePathField = new JTextField(20);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseButtonListener(filePathField));
        filePanel.add(filePathLabel);
        filePanel.add(filePathField);
        filePanel.add(browseButton);

        JPanel sendTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sendTypeLabel = new JLabel("Send Type:");
        String[] sendTypes = {"Prediction", "Training"};
        sendTypeComboBox = new JComboBox<>(sendTypes);
        sendTypePanel.add(sendTypeLabel);
        sendTypePanel.add(sendTypeComboBox);

        JPanel clientIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel clientIdLabel = new JLabel("Client ID:");
        clientIdField = new JTextField(10);
        clientIdPanel.add(clientIdLabel);
        clientIdPanel.add(clientIdField);

        inputPanel.add(filePanel);
        inputPanel.add(sendTypePanel);
        inputPanel.add(clientIdPanel);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        fileContentTextArea = new JTextArea();
        fileContentTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(fileContentTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton sendFilesButton = new JButton("Send Files");
        sendFilesButton.addActionListener(new SendFilesButtonListener());
        buttonPanel.add(sendFilesButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(mainPanel);
        setVisible(true);
    }

    private class BrowseButtonListener implements ActionListener {
        private JTextField targetField;

        public BrowseButtonListener(JTextField targetField) {
            this.targetField = targetField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(ClientInterface.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                targetField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    private class SendFilesButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filePath = filePathField.getText();
            String sendType = (String) sendTypeComboBox.getSelectedItem();
            String clientId = clientIdField.getText();

            if (!filePath.isEmpty() && !clientId.isEmpty()) {
                try {
                    ClientImpl client = new ClientImpl();
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    registry.bind("Client" + clientId, client);

                    String fileName = new File(filePath).getName();

                    if (sendType.equals("Prediction")) {
                        client.sendPredictionFile(filePath, fileName, clientId);
                    } else if (sendType.equals("Training")) {
                        client.sendTrainingFile(filePath, fileName, clientId);
                    }

                    // Display the file content after receiving
                    System.out.println(clientId + "_files" + '/' + clientId+'_' + filePath);

                    displayFileContent(clientId + "_files" + '/' + clientId+'_'+filePath);


                    JOptionPane.showMessageDialog(ClientInterface.this, "File sent successfully.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientInterface.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(ClientInterface.this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayFileContent(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            try {
                StringBuilder content = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                fileContentTextArea.setText(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File does not exist: " + filePath);
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientInterface::new);
    }
}
