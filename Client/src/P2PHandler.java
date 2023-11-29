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
            File file = new File(filePath);
            byte[] fileBytes = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(fileBytes, 0, fileBytes.length);

            OutputStream os = socket.getOutputStream();
            os.write(fileBytes, 0, fileBytes.length);
            os.flush();
            bis.close();
            os.close();
            socket.close();
            System.out.println("File" + fname + "sent to client" + socket.getInetAddress().getHostAddress() + "successfully");
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