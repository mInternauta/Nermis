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

/**
 * Nermis Configuration 
 */
public class nConfiguration {
    /**
    * Nermis Built-in Web Server Port
    */
    public int WebServerPort;
    
    /**
     * Nermis Display Language
     * Seek for built-in language, if this not exists seek for language files in
     * /Data/Langs/
     * If this language not exists, load the default "english"
     */
    public String Language;   
    
    /**
     * Installed Watchers Jars
     */
    public HashMap<String, String> WatchersJars;
}