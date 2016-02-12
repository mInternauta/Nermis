package mInternauta.Nermis.Net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;



/**
 * Utilities for Sockets
 */
public final class SocketUtils {
    /**
     * Check if the port is open 
     * @param hostname
     * @param port
     * @return 
     */
    public static boolean checkUdpPort(String hostname, int port) {
        boolean results = false;
        
        try {
            DatagramSocket dtSocket = new DatagramSocket();
            dtSocket.connect(InetAddress.getByName(hostname), port);
            if(dtSocket.isConnected()) {
                results = true;
                dtSocket.close();
            }
        } catch (Exception ex) {
            LastException = ex;
            results = false;
        }
        
        return results;
    }
    
    /**
     * Check if the port is open 
     * @param hostname
     * @param port
     * @return 
     */
    public static boolean checkTcpPort(String hostname, int port) {
        boolean results = false;
        
        try {
            Socket socket = new Socket(hostname, port);
            if(socket.isConnected()) {
                results = true;
                socket.close();
            }
        } catch (Exception ex) {
            LastException = ex;
            results = false;
        }
        
        return results;
    }
    
    public static Exception LastException;
}
