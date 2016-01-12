package pl.rcebula.volumecontroller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by robert on 11.01.16.
 */
public class Client
{
    private final String OPEN_URL = "OPEN_URL";
    private final String CLOSE_URL = "CLOSE_URL";
    private final String SET_VOLUME = "SET_VOL";
    private final String GET_VOLUME = "GET_VOL";

    private final String host;
    private final int port;

    public Client(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    private Socket connectToServer() throws IOException
    {
        InetAddress serverAddr = InetAddress.getByName(host);

        Socket socket = new Socket(serverAddr, port);

        return socket;
    }

    public int getVolume() throws IOException
    {
        Socket socket = connectToServer();

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                "UTF-8"));

        out.writeBytes(GET_VOLUME + "\n");
        String answer = in.readLine();

        socket.close();

        return Integer.parseInt(answer);
    }

    public void setVolume(int volume) throws IOException
    {
        Socket socket = connectToServer();

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String toWrite = SET_VOLUME + " " + Integer.toString(volume) + "\n";

        out.writeBytes(toWrite);

        socket.close();
    }

    public void openURL(String url) throws IOException
    {
        Socket socket = connectToServer();

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String toWrite = OPEN_URL + " " + url + "\n";

        out.writeBytes(toWrite);

        socket.close();
    }

    public void closeURL() throws IOException
    {
        Socket socket = connectToServer();

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String toWrite = CLOSE_URL + "\n";

        out.writeBytes(toWrite);

        socket.close();
    }
}
