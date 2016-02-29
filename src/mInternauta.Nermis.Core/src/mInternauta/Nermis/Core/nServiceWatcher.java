package mInternauta.Nermis.Core;

import java.util.ArrayList;
import java.util.HashMap;

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


/**
 * Interface for Service Watcher
 * <p>
 * Watcher are routines that analyze the state of the service, 
 * they are scheduled and can run several times,
 * monitoring the current state of the service and considering whether it is operational or not.
 */
public interface nServiceWatcher {
    /**
     * Get the watcher name
     * @return 
     */
    public String getName();
    
    /**
     * Execute the analyze routine
     * @param service Service to execute
     * @param context Context 
     * @return Results for the service analyze
     */
    public nServiceResults execute(nService service, nServiceWatcherContext context);
    
    /**
     * Examines whether the service is correctly configured for this watcher
     * @param service
     * @return 
     */
    public boolean validate(nService service);
    
    
    /**
     * Returns a list of all extended properties required by the Watcher and the explanation of each property.
     * @return 
     */
    public HashMap<String,String> getExtPropertiesHelp();
    
    /**
     * 
     * @return 
     */
    public ArrayList<nRRDDatasource> getRRDDatasources();
}
