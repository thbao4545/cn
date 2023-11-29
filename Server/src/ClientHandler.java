import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public Socket socket;
    public BufferedReader in;
    public BufferedWriter out; 
    public List<String> clientFile = new ArrayList<>();
    public String IPAddress;
    public Socket bash = null;
    public boolean isBashConnected = false;
    public String command;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            IPAddress = socket.getInetAddress().getHostAddress();
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            clientHandlers.add(this);
            System.out.println("Client connected: " + IPAddress);
        }
        catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                if (isBashConnected) {
                    handleBashMessage();
                
                    bash.close();
                    isBashConnected = false;
                }
                if (in.ready()) {
                    handleClientMessage();
                }
            }
            catch (IOException e) {
                closeEverything(socket, in, out);
            }
        }
    }   
    public void handleBashMessage() {
        try {
            BufferedWriter bashOut = new BufferedWriter(new OutputStreamWriter(bash.getOutputStream()));
            switch (command) {
                case "ping":
                    socket.setSoTimeout(5000); // Set a timeout of 5 seconds
                    // Send ping to client
                    out.write("ping");
                    out.newLine();
                    out.flush();
                    // Wait for pong from client
                    String clientMessage = in.readLine();
                    try {
                        if (clientMessage != null && clientMessage.equals("pong")) {
                            bashOut.write("Client is connected.");
                            bashOut.newLine();
                            bashOut.flush();
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        System.out.println("Timeout occurred while waiting for response from client.");
                    }
                    break;
                case "discover":
                    out.write("discover");
                    out.newLine();
                    out.flush();
                    String filename;
                    while ((filename = in.readLine()) != null) {
                        if (filename.equals("end")) break;
                        bashOut.write(filename);
                        bashOut.newLine();
                        bashOut.flush();
                    }
                    
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleClientMessage() {
        try {
            String clientMessage = in.readLine();
            String[] args = clientMessage.split(" ");
            switch (args[0]) {
                case "publish":
                    System.out.println("Publish received.");
                    if (fileCheck(args[2])) {
                        // Informing client file already exists
                        out.write("File's name existed");
                        out.newLine();
                        out.flush();
                    }
                    else {
                        out.write("Server informed of file named " + args[2] + " from " + IPAddress);
                        out.newLine();
                        out.flush();
                        clientFile.add(args[2]);
                    }
                    break;
                case "fetch":
                    String fileName = args[1];
                    for (ClientHandler clientHandler : clientHandlers) {
                        for (String fname : clientHandler.clientFile) {
                            if (fileName.equals(fname)) {
                                // File exists
                                out.write(clientHandler.IPAddress);
                                out.newLine();
                                out.flush();
                                break;
                            }
                        }
                    }
                    out.write("Cannot find file named" + fileName);
                    out.newLine();
                    out.flush();
                    break;
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }

    }
    public Boolean fileCheck(String fname) {
        for (String fileName : clientFile) {
            if (fileName.equals(fname))
                return true;
        }
        return false;
    }

    public void removeClient() {
        clientHandlers.remove(this);
        return;
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        removeClient();
        try {
            if (!socket.isClosed()) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}