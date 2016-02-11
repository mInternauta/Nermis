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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import mInternauta.Nermis.nResourceHelper;
import mInternauta.Nermis.nController;

/**
 * Manage the display language files
 */
public class nLanguage {
    /**
     * Load the language
     * @param lang
     * @return 
     */
    public static Properties load(String lang) {        
        Properties props = new Properties();
                
        
        try {
            InputStream stream = nLanguage.class.getResourceAsStream("/assets/Nermis/Langs/" + lang.trim() + ".properties");
            
            if(stream == null) {
                File path = nResourceHelper.BuildName("Langs", lang);
                if(path.exists()) {
                    stream = new FileInputStream(path);
                } else {
                    stream = nLanguage.class.getResourceAsStream("/assets/Nermis/Langs/english.properties");
                }
            }
            
            props.load(stream);            
        } catch (IOException ex) {
          nController.CurrentLogger.log(Level.SEVERE, null, ex);
        }
       
        return props;
    }
}
