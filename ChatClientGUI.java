import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
We will now design a simple graphical user interface for our chat client using Java Swing. For our chat app, we'll focus
on several Swing components, including frames, text areas, buttons, and text fields.
 */


public class ChatClientGUI extends JFrame {
    private JTextArea messageArea; // for displaying messages
    private JTextField textField; // for typing new messages
    private ChatClient client;
    private JButton exitButton; // exit button to close GUI and app

    public ChatClientGUI() {
        super("Chat Application");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Styling variables
        Color backgroundColor = new Color(240,240,240); // light gray background
        Color buttonColor = new Color(75,75,75); // Darker gray for buttons
        Color textColor = new Color(50,50,50); // Almost black for text
        Font textFont = new Font("Arial",Font.PLAIN,14);
        Font buttonFont = new Font("Arial",Font.BOLD,12);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        // Apply styles to the message area
        messageArea.setBackground(backgroundColor);
        messageArea.setForeground(textColor);
        messageArea.setFont(textFont);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.sendMessage(textField.getText());
                textField.setText("");
            }
            // when the user presses Enter after typing a message in the JTextField, the message is sent to the server
            // via the ChatClient instance
        });
        add(textField, BorderLayout.SOUTH);

        // Initialize and start the ChatClient
        try {
            this.client = new ChatClient("127.0.0.1", 5000, this::onMessageReceived);
            // the constructor tries to initialize the ChatClient with the server's address and port and a method
            // reference (this::onMessageReceived) to handle incoming messages
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Prompt for username
        String name = JOptionPane.showInputDialog(this, "Enter your name:",
                "Name Entry", JOptionPane.PLAIN_MESSAGE);
        // Update the window title to include username
        this.setTitle("Chat Application - " + name);

        // Apply styles to the text field
        textField = new JTextField();
        textField.setFont(textFont);
        textField.setForeground(textColor);
        textField.setBackground(backgroundColor);
        // Modify actionPerformed to include the username and timestamp
        textField.addActionListener(e -> {
            String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + name + ": " +
                    textField.getText();
            client.sendMessage(message); // sends message string to server
            textField.setText(""); // after sending, text field is cleared and ready for next message
        });

        // Apply styles to the exit button and initialize it
        exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);
        exitButton.setBackground(buttonColor);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> {
            // Send a departure message to the server
            String departureMessage = name + " has left the chat.";
            client.sendMessage(departureMessage);

            // Delay to ensure the message is sent before exiting
            try {
                Thread.sleep(1000); // Wait for 1 second to ensure message is sent
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            // Exit the application
            System.exit(0);
        });
        // Creating a bottom panel to hold the text field and exit button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor); // Apply background color to the panel
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(exitButton, BorderLayout.EAST); // Add the exit button to the bottom panel
        add(bottomPanel, BorderLayout.SOUTH); // Add the bottom panel to the frame
    }

    private void onMessageReceived(String message) {
        // Use SwingUtilities.invokeLater to ensure thread safety when updating the GUI
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        // Ensure the GUI is created and updated on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new ChatClientGUI().setVisible(true);
        });
    }
}
