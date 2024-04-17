import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class ClientInterface extends JFrame {

    private JTextField filePathField;
    private JComboBox<String> sendTypeComboBox;
    private JTextField clientIdField;

    public ClientInterface() {
        setTitle("Client Interface");
        setSize(400, 200);
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton sendFilesButton = new JButton("Send Files");
        sendFilesButton.addActionListener(new SendFilesButtonListener());
        buttonPanel.add(sendFilesButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientInterface());
    }
}
