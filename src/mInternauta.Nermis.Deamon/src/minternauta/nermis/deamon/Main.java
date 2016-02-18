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
package minternauta.nermis.deamon;

import mInternauta.Nermis.nController;

public class Main {    

    public static nController Controller;
    private static boolean isInCLI;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        System.out.println("mInternauta Solutions (C) 2016");
        System.out.println("Nermis - Remote Service Watcher");
                
        Controller = new nController();
        
        // - Check if is in service mode
        isInCLI = true;
        for(String arg : args) {
            if(arg.trim().equalsIgnoreCase("--service")) {
                isInCLI = false;
                break;
            }
        }
        
        // - Startup the Controller if is in Service Mode
        if(isInCLI == false) {
            enterServiceMode();
        }       
        else 
        {
            enterCLIMode();
        }
    }

    private static void enterServiceMode() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Controller.Stop();
                Thread.currentThread().interrupt();
            }
        });
        
        Controller.Start();
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    private static void enterCLIMode() {
        CLIManager manager = new CLIManager();
        manager.Setup();
        manager.Run();            
    }
    
}
