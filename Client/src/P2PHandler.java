import java.io.*;
import java.net.Socket;

public class P2PHandler implements Runnable
{
    public Socket socket;

    public P2PHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        String fname;
        try {
            // Getting file name
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            fname = bufferedReader.readLine();
            String filePath = "../repository/" + fname;
            int bytes = 0;
            // Open the File where he located in your pc
            File file = new File(filePath);
            FileInputStream fileInputStream
                = new FileInputStream(file);
    
            // Here we send the File to Server
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeLong(file.length());
            // Here we  break file into chunks
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer))
                != -1) {
            // Send the file to Server Socket  
            dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            // close the file here
            fileInputStream.close();
            System.out.println("File sent successfully");
 
        }
        catch (IOException e) {
            closeSocket(socket);
        }
    }   

    public void closeSocket(Socket socket)
    {
        try {
            if (socket!=null) socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}