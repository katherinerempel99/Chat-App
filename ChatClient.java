import java.io.*;
import java.net.*;
import java.util.function.Consumer;
/*
The client needs to establish a connection to the server using a Socket. This requires the server's IP address and the
port number on which the server is listening.

Client should be able to send messages to the server with output stream and writing messages to it. It also needs to
continuously listen for messages from the server by setting up an input stream and reading from it in a loop.
 */
public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> onMessageReceived;
    public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException {
        // establishes a connection to the chat server at the specified address and port.
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.onMessageReceived = onMessageReceived;
        System.out.println("Connected to the chat server");

        /* obsolete with GUI
            inputConsole = new BufferedReader(new InputStreamReader(System.in));
            // reads messages from console and sends them to server
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line = "";
            while (!line.equals("exit")) { // client runs in a loop until user types "exit"
                line = inputConsole.readLine();
                out.println(line);
                System.out.println(in.readLine());
            }

            socket.close();
            inputConsole.close();
            out.close(); */
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    onMessageReceived.accept(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


}
