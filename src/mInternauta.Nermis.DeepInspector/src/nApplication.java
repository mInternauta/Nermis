
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

public final class nApplication {

    public static Logger CurrentLogger = Logger.getLogger("DeepInspector");
    
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
            FileHandler fileLog = new FileHandler("./DeepInspector.log", 1000024, 5);
            fileLog.setFormatter(new SimpleFormatter());
            CurrentLogger.addHandler(fileLog);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(nApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
