import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Client implements Runnable{
    private Socket socket;
    private ServerSocket bashSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private Socket bash;
    private boolean isBashConnected = false;

    public Client(Socket socket, ServerSocket bashSocket) {
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.bashSocket = bashSocket;
        } catch (IOException e) {
            closeEverything(socket, in, out, bashSocket);
        }   
    }

    @Override
    public void run() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                handleMessage();
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
    public void handleMessage() {
        while (socket.isConnected()) {
            try {
                if (bash != null && isBashConnected) {
                    // System.out.println("Bash script handle.");
                    handleBashMessage();
                
                    bash.close();
                    isBashConnected = false;
                }
                if (in.ready()) {
                    handleServerMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void handleServerMessage() {
        try {
            String messageFromServer = in.readLine();
            switch (messageFromServer) {
                case "ping":
                    System.out.println("Ping received.");
                    out.write("pong");
                    out.newLine();
                    out.flush();
                    System.out.println("Ping send back.");
                    break;
                case "discover":
                    System.out.println("Discover received.");
                    File folder = new File("../repository");
                    File[] files = folder.listFiles();
        
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile()) {
                                out.write(file.getName());
                                out.newLine();
                            }
                        }
                        out.write("end");
                        out.newLine();
                        System.out.println("Discover finished.");
                    }
                    else {
                        out.write("No files in repository.");
                    }
                    out.flush();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    //Checking connection from local bash script
    public void bashConnection() {
        while (!bashSocket.isClosed()) {
            try {
                bash = bashSocket.accept();
                isBashConnected = true;
                // System.out.println("Bash script connected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Handling bash script message
    public void handleBashMessage() {
            try {
                BufferedReader bashIn = new BufferedReader(new InputStreamReader(bash.getInputStream()));
                BufferedWriter bashOut = new BufferedWriter(new OutputStreamWriter(bash.getOutputStream()));
                String bashMessage = bashIn.readLine();
                String[] args = bashMessage.split(" ");
                switch (args[0]) {
                    case "fetch":
                        String filename = args[1];
                        String folderPath = "../repository"; 
                        
                        File folder = new File(folderPath);
                        File[] files = folder.listFiles();
                        
                        boolean fileExists = false;
                        if (files != null) {
                            for (File file : files) {
                                if (file.isFile() && file.getName().equals(filename)) {
                                    fileExists = true;
                                    break;
                                }
                            }
                        }
                        
                        if (fileExists) {
                            bashOut.write("File exists in the folder.");
                            bashOut.newLine();
                            bashOut.flush();
                            return;
                        } 
                        break;
                }
                out.write(bashMessage + "\n");
                out.flush();
                
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    switch (args[0]) {
                        case "fetch":
                            try (Socket download = new Socket(serverResponse, 1235)) {  
                                String destinationPath = "../repository/" + args[1];
                                byte[] fileBytes = new byte[1024];
                                InputStream is = download.getInputStream();
                                FileOutputStream fos = new FileOutputStream(destinationPath);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);
        
                                int bytesRead;
                                while ((bytesRead = is.read(fileBytes, 0, fileBytes.length)) != -1) {
                                    bos.write(fileBytes, 0, bytesRead);
                                }
        
                                bos.flush();
                                System.out.println("File downloaded successfully");
        
                                bos.close();
                                fos.close();
                                is.close();
                                download.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            
                            break;
                    
                        default:
                            bashOut.write(serverResponse);
                            bashOut.newLine();
                            bashOut.flush();
                            break;
                    }
                    break;
                } 
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    
    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out, ServerSocket bashSocket) {
        try {
            if (!socket.isClosed()) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (!bashSocket.isClosed()) bashSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    public static void main(String[] args) throws Exception{
        String host = "localhost";
        if(args.length == 1) {
            host = args[0];
        }
        System.out.println("Client application Started");
        //Open port for command line interface
        ServerSocket bashSocket = new ServerSocket(1232);
        //Connect to server
        Socket socket = new Socket(host, 1234);
        Client client = new Client(socket, bashSocket);
        Thread clientThread = new Thread(client);
        clientThread.start();
        //Open port for peer's connection
        ServerSocket serverSocket = new ServerSocket(1235);
        P2P server = new P2P(serverSocket);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
}
