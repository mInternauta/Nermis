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
 * @author marcelo
 */
public final class nApplication {    
    /**
     * Global logger for the Nermis 
     */
    public static final Logger CurrentLogger = Logger.getLogger("NermisLogger");   
    
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
    
    private static void setupLogger() 
    {       
        try {
            // Setup the Logger
            CurrentLogger.setLevel(Level.ALL);
            CurrentLogger.setUseParentHandlers(false);
            CurrentLogger.addHandler(new ConsoleHandler());
            FileHandler fileLog = new FileHandler(nResourceHelper.BuildName("Logs", "Global").getAbsolutePath(), 1000024, 5);
            fileLog.setFormatter(new SimpleFormatter());
            CurrentLogger.addHandler(fileLog);
        } catch (IOException ex) {
            Logger.getLogger(nApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(nApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
