import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2P implements Runnable 
{
    private ServerSocket serverSocket;

    public P2P(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                P2PHandler clientHandler = new P2PHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket!=null) serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
