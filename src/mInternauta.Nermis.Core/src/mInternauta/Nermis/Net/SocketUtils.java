package mInternauta.Nermis.Net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mInternauta.Nermis.Core.nServiceState;

/*
 * Copyright (C) 2015 mInternauta
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */


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
