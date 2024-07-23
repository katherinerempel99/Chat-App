import java.io.*;
import java.net.*;
import java.util.*;
/*
ChatServer class starts a server that listens on port 5000. For each connecting client, it spawns a new thread
represented by the ClientHandler class.
 */
public class ChatServer {
    // List to keep track of all connected clients
    private static List<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000); // port number 5000
        System.out.println("Server started. Waiting for clients...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected." + clientSocket);
            // once a server is up, it needs to continuously listen for new client connections and accept them.
            // typically done in a loop, where for each accepted connection, a new 'Socket' is created to handle client
            // communication

            // Spawn a new thread for each client (to allow multiple clients to connect at once)
            ClientHandler clientThread = new ClientHandler(clientSocket, clients);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
        // Additional code to handle communication will be added later. Sending messages from the client to the server
        // and then broadcasting those messages to other clients to create a basic chat room.
    }
}
/*
The ClientHandler class handles all communication with a connected client. It reads messages sent by the client and
broadcasts them to all other clients. We maintain a static list of all client handlers (clients) to keep track of
connected clients and facilitate message broadcasting.
 */
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Broadcast message to all clients
                for (ClientHandler aClient : clients) {
                    aClient.out.println(inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
