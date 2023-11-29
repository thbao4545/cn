import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private ServerSocket bashSocket;

    public Server(ServerSocket serverSocket, ServerSocket bashSocket) {
        this.serverSocket = serverSocket;
        this.bashSocket = bashSocket;
    }

    @Override
    public void run() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        });
        thread1.start();
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                bashConnection();
            }
        });
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e) {
            closeEverything(serverSocket, bashSocket);
        }
    }
    public void bashConnection() {
        while (!bashSocket.isClosed()) {
            try {
                Socket bashScriptSocket = bashSocket.accept();
                BufferedReader bashIn = new BufferedReader(new InputStreamReader(bashScriptSocket.getInputStream()));
                BufferedWriter bashOut = new BufferedWriter(new OutputStreamWriter(bashScriptSocket.getOutputStream()));
                String bashMessage = bashIn.readLine();
                String[] args = bashMessage.split(" ");
                System.out.println(bashMessage);
                Boolean found = false;
                for (ClientHandler client : ClientHandler.clientHandlers) {
                    if (client.IPAddress.equals(args[1])) {
                        // System.out.println("Bash script connected to client: " + client.IPAddress);
                        client.command = args[0];
                        client.bash = bashScriptSocket;
                        client.isBashConnected = true;
                        found = true;
                    }
                }
                if (!found) {
                    bashOut.write("Client not found.");
                    bashOut.newLine();
                    bashOut.flush();
                    bashScriptSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void closeEverything(ServerSocket serverSocket, ServerSocket bashSocket) {
        try {
            if (serverSocket != null) serverSocket.close();
            if (bashSocket != null) bashSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        System.out.println("Server application started");
        //Open port for command line interface
        ServerSocket bashSocket = new ServerSocket(1233);
        //Open port for client's connection
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket, bashSocket);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
