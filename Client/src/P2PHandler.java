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
            byte[] buffer = new byte[4096];

            FileInputStream fis = new FileInputStream(filePath);
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