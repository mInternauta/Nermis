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
package mInternauta.Nermis.Configs;

import java.util.HashMap;
import java.util.Properties;
import mInternauta.Nermis.Persistence.nStorage;

/**
 * Helps manage the Nermis Main Configuration 
 */
public class nConfigHelper {

    private static nConfiguration cConfig;
    private static Properties cDisplayLang;
    
    /**
     * Get the configuration
     * @return 
     */
    public static nConfiguration getConfiguration() {
        if(cConfig == null) {
            cConfig = Load();
        }
        return cConfig;
    }
    
    /**
     * Get the display language
     * @return 
     */
    public static Properties getDisplayLanguage() {
        if(cDisplayLang == null) {
            cDisplayLang = nLanguage.load(getConfiguration().Language);
        }
        
        return cDisplayLang;
    }
    
    /**
     * Load the configuration from the disk resource
     * @return The configuration object
     */
    public static nConfiguration Load() {
        nStorage storage = nStorage.getInstance();
        nConfigContainer container = storage.loadContainer("Nermis", "Settings");
        nConfiguration cfg = null;
        
        if(container == null) {
            cfg = getDefaults();
            Save(cfg);
        } else 
        {
           cfg = container.getMyData();
           if(cfg == null) {
               cfg = getDefaults();
               Save(cfg);
           }
        }        
                   
        return cfg;
    }
    
    /**
     * Save the configuration to the disk resource
     * @param cfg 
     */
    public static void Save(nConfiguration cfg) {
        nStorage storage = nStorage.getInstance();
        nConfigContainer container = new nConfigContainer();        
        container.setMyData(cfg);
        storage.saveContainer(container, "Nermis", "Settings");
        
        nConfigHelper.cConfig = cfg;
    }

    /**
     * Gets the default configuration for Nermis
     * @return 
     */
    public static nConfiguration getDefaults() {
        nConfiguration cfg = new nConfiguration();
        cfg.WebServerPort = 5000;
        cfg.Language = "english";
        cfg.WatchersJars = new HashMap<>();
        cfg.WatchersJars.put("Builtin Watchers", "./lib/mInternauta.Nermis.Builtin");
        
        return cfg;
    }
}
