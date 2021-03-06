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
package mInternauta.Nermis.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;

/**
 * Manage the Web Template for web pages
 */
public class nTemplateManager {
    /**
     * Loads a template 
     * If the resource not exists in the disk, try to seek for the default template.
     * @param name
     * @return 
     */
    public String load(String name) {
        String template = "";
        
        validate(name);
        
        try {
            InputStream stream = nResourceHelper.ReadResource(name, "Templates");
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, Charset.defaultCharset());
            
            template = writer.toString();
        } catch (Exception ex) {
            CurrentLogger.logp(Level.SEVERE, "Templates", "Load", ex.getLocalizedMessage());
        }
        
        return template;
    }
    
    /**
     * Check if the template is in disk if not try to store the default template.
     * @param name 
     */
    public void validate(String name)
    {
        try 
        {
            if(nResourceHelper.ExistsResource(name, "Templates") == false) {
                InputStream template = nTemplateManager.class.getResourceAsStream("/assets/Nermis/Templates/" + name);
                if(template != null) {
                    OutputStream output = nResourceHelper.WriteResource(name, "Templates");
                    IOUtils.copy(template, output);
                    output.flush();
                    output.close();
                }
            } 
        } catch (Exception ex) {
            CurrentLogger.logp(Level.SEVERE, "Templates", "Validate", ex.getLocalizedMessage());
        }
    }
}
