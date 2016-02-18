
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) 2016 mInternauta
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
 * DeepInspector 
 * it's a simple binary TCP server that responds to any message sent, can be used to test connection to go through a firewall or suffers with any redirection.
 */
public class Main {

    private static ServerSocket server;
    private static Thread srvThread;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("mInternauta - Copyright 2016");
            System.out.println("Nermis DeepInspector");
            
            // - Start the server
            server = new ServerSocket(5050);            
            createThread();            
            srvThread.start();
            
            System.out.println("Server is online");            
            // - Runtime Shutdown
            setRuntimeShutdown();
            
            while(true) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException ex) {
             System.out.println("Server Error: " + ex);
        }
    }

    private static void setRuntimeShutdown() {
        // -
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    srvThread.interrupt();
                    srvThread.join();
                    server.close();
                    
                    Thread.currentThread().interrupt();
                } catch (InterruptedException | IOException ex) {
                    System.out.println("Server Error: " + ex);
                }
            }
        });
    }

    private static void createThread() {
        // - Server Thread
        srvThread = new Thread() {
            @Override
            public void run() {
                while(this.isInterrupted() == false) {
                    try {
                        Socket connSocket = server.accept();
                        
                        System.out.println("New connection. Waiting for data...");
                        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
                        DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
                        
                        int cRead = inFromClient.read();
                        int readed = 0;
                        while(cRead != -1)
                        {
                            outToClient.write(cRead);
                            readed ++;
                            cRead = inFromClient.read();
                        }
                        
                        outToClient.flush();
                        System.out.println("Finished transferred: " + String.valueOf(readed) + " characters");
                    } catch (IOException ex) {
                        System.out.println("Server Socket Error: " + ex);
                    }
                }
            }
        };
    }
    
}
