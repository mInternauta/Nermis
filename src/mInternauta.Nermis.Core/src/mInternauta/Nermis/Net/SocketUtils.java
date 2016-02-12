package mInternauta.Nermis.Net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;



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
            SocketAddress sockaddr = new InetSocketAddress(InetAddress.getByName(hostname), port);
            Socket socket = new Socket();
            
            socket.connect(sockaddr, 1000);
            
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
