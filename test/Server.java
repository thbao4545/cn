import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

public class Server {

    public static void sendFile(Socket socket, String filePath) throws IOException {
        File file = new File(filePath);
        byte[] buffer = new byte[4096];

        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream os = socket.getOutputStream();

        int bytesRead;
        while ((bytesRead = bis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        os.flush();
        bis.close();
        fis.close();
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serversocket = new ServerSocket(6666);
        Socket socket = serversocket.accept();
        sendFile(socket, ".\\test.txt");
    }

}
