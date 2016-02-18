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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * Helps manage all resources in disk 
 */
public class nResourceHelper {
    /**
     * Open a read stream for a resource
     * @param name The name of the resource
     * @param kind The resource kind, Pre-defined kinds are:
     *             Settings (Will save the resource with .xml)
     *             Logs (Will save the resource with .log)
     *             Langs (Will save the resource with .properties)
     *             others resources kind will save the resource with .nmd
     * @return The Input Stream for the resource
     * @throws Exception 
     */
    public static InputStream ReadResource(String name, String kind) throws Exception {
        File file = BuildName(kind, name);
        return new FileInputStream(file);
    }
    
    /**
     * Open a write stream for a resource
     * @param name The name of the resource
     * @param kind The resource kind, Pre-defined kinds are:
     *             Settings (Will save the resource with .xml)
     *             Logs (Will save the resource with .log)
     *             Langs (Will save the resource with .properties)
     *             others resources kind will save the resource with .nmd
     * @return The Output Stream for the Resource
     * @throws Exception 
     */
    public static OutputStream WriteResource(String name, String kind)  throws Exception {
        File file = BuildName(kind, name);
        return new FileOutputStream(file);
    }
    
    /**
     * Check if the resource exists in the Disk
     * @param name The name of the resource
     * @param kind The resource kind, Pre-defined kinds are:
     *             Settings (Will save the resource with .xml)
     *             Logs (Will save the resource with .log)
     *             Langs (Will save the resource with .properties)
     *             others resources kind will save the resource with .nmd
     * @return True if the resource exists, otherwise returns false.
     */
    public static boolean ExistsResource(String name, String kind) {
        File file = BuildName(kind, name);;
        return file.exists();
    }

    /**
     * Build the File Object from the resource name and kind
     * <p>
     * THis methods will create all directories from the resource path. 
     * @param name The name of the resource
     * @param kind The resource kind, Pre-defined kinds are:
     *             Settings (Will save the resource with .xml)
     *             Logs (Will save the resource with .log)
     *             Langs (Will save the resource with .properties)
     *             others resources kind will save the resource with .nmd
     * @return The FIle Object
     */
    public static File BuildName(String kind, String name) {
        String path = BuildDirectory(kind);
        
        path += name;
        
        if(kind.equalsIgnoreCase("Settings")) {
            path += ".xml";
        } else if(kind.equalsIgnoreCase("Logs")) {
            path += ".log";
        } else if(kind.equalsIgnoreCase("Langs")) {
            path += ".properties";
        } else {
            path += ".nmd";
        }
        
        File file = new File(path);
        return file;
    }

    /**
     * Build the directory path and create it if not exists
     * @param kind
     * @return 
     */
    public static String BuildDirectory(String kind) {
        String path = "./Data/" + kind + "/";
        File directory = new File(path);
        if(directory.exists() == false) {
            directory.mkdirs();
        }
        return path;
    }

    public static void CopyEmbedded(String embedded, String path) {
        try {
            InputStream stream = nResourceHelper.class.getResourceAsStream(embedded);
            if(stream != null) {
                File finalPath = new File(path);
                if(finalPath.exists() == false) {
                    try (FileOutputStream output = new FileOutputStream(finalPath)) {
                        IOUtils.copy(stream, output);
                        output.flush();
                    }
                }
                stream.close();
            }
        }
        catch(Exception ex)
        {
            Logger.getGlobal().log(Level.WARNING, ex.getLocalizedMessage());
        }
    }
}
