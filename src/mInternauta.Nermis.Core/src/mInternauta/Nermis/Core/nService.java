package mInternauta.Nermis.Core;

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


import java.util.HashMap;

/**
 * Nermis Service Definition
 */
public class nService {
    /**
    * Service Name
    * <p>
    * Property name = name
    */
    public String Name;
    
    /**
    * Service Description
    * <p>
    * Property name = description
    */
    public String Description;
    
    /**
    * Service Ref Url    
    * <p>
    * Property name = refurl
    */
    public String RefUrl;
    
    /**
    * Scheduler - Repeat Every x seconds 
    * If the RepeatEvery is 0 or lesser the watcher will only run in the startup
    * <p>
    * Property name = repeatevery
    * Need to be a numeric value
    */
    public int RepeatEvery;   
    
    /**
    * Watcher Name
    * <p>
    * Property name = watcher
    */
    public String Watcher;
    
    /**
    * Watcher Properties
    * <p>
    * See the watchers extends properties and the web server extended property for services
    * @see mInternauta.Nermis.Watchers.*
    */
    public HashMap<String,String> Properties;
}
