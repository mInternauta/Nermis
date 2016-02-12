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
package mInternauta.Nermis.Notifiers;

import java.util.HashMap;
import mInternauta.Nermis.Core.nService;

/**
 * Abstract Interface for Notifiers
 */
public abstract class nAbstractNotifier {

    private HashMap<String, String> properties = new HashMap<>();
    
    /**
     * Occurs when a service change to offline state
     * @param service
     * @param message
     */
    public abstract void NotifyServiceOffine(nService service, String message);
    
    /**
     * Notify a Server error
     * @param message 
     */
    public abstract void NotifyServerError(String message);
    
    /**
     * Configure the Notifier
     * @param props 
     */
    public void setProperties(HashMap<String,String> props) 
    {
        this.properties = props;
    }
    
    /**
     * Return the value of a property
     * @param name
     * @return 
     */
    protected String getProperty(String name) 
    {
        return this.properties.getOrDefault(name, "");
    }
    
    /**
     * Get a list of required properties
     * @return 
     */
    public abstract String[] getProperties();
    
    /**
     * The notifier name
     * @return 
     */
    public abstract String getName();
}
