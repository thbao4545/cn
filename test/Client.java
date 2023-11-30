import java.net.Socket;
import java.io.*;
import java.net.Socket;

public class Client {
    public static void receiveFileFromSocket(Socket socket, String filePath) {
        try {
            InputStream inputStream = socket.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            inputStream.close();
            socket.close();

            System.out.println("File received successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Usage example
        try {
            Socket socket = new Socket("localhost", 6666);
            receiveFileFromSocket(socket, "./file.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
