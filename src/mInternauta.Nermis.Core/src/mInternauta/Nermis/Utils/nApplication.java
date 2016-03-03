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
package mInternauta.Nermis.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Notifiers.nAbstractNotifier;
import mInternauta.Nermis.Notifiers.nConsoleNotifier;

/**
 * Current Application Environment
 */
public final class nApplication {    
    /**
     * Global logger for the Nermis 
     */
    public static Logger CurrentLogger;   
    
    /**
     * Loaded Watchers
     */
    public static ArrayList<nServiceWatcher> Watchers = new ArrayList<>();
    
        
    /**
     * Loaded Notifiers
     */
    public static ArrayList<nAbstractNotifier> Notifiers = new ArrayList<>();
       
    /**
     * Get Current notifier
     * @return 
     */
    public static nAbstractNotifier getNofitier() 
    {
        nAbstractNotifier notifier = new nConsoleNotifier();
        
        if(nConfigHelper.getConfiguration().Notifier != null && nConfigHelper.getConfiguration().Notifier.isEmpty() == false) {
            for(nAbstractNotifier _cNotifier : Notifiers) {
                if(_cNotifier.getName().equalsIgnoreCase(nConfigHelper.getConfiguration().Notifier)) {
                    notifier = _cNotifier;
                    
                    if(nConfigHelper.getConfiguration().NotifierProperties != null) {
                        notifier.setProperties(nConfigHelper.getConfiguration().NotifierProperties);
                    }
                    
                    break;
                }
            }
        }
        
        return notifier;
    }  
    
    // -
    static {
        setupLogger();
    }
    
    public static Logger CreateLogger(String logName, boolean notOutConsole)
    {
        Logger cLogger = null;
        
         try {
            // Setup the Logger
            cLogger = Logger.getLogger("Nermis-" + logName);
            cLogger.setLevel(Level.ALL);
            cLogger.setUseParentHandlers(false);
            
            if(notOutConsole == false) {
                cLogger.addHandler(new ConsoleHandler());
            }
            
            FileHandler fileLog = new FileHandler(nResourceHelper.BuildName("Logs", logName).getAbsolutePath(), 1000024, 5);
            fileLog.setFormatter(new SimpleFormatter());
            cLogger.addHandler(fileLog);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(nApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return cLogger;
    }
    
    private static void setupLogger() 
    {       
       CurrentLogger = CreateLogger("Global", false);
    }
}
