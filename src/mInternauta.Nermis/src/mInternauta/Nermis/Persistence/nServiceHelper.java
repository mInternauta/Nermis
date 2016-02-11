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
package mInternauta.Nermis.Persistence;

import java.util.ArrayList;
import java.util.HashMap;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceStateTable;

/**
 * Encapsulates the basic operations with the Storage Service 
 */
public class nServiceHelper {
    /**
     * Add a service to the storage
     * @param service 
     */
    public static void AddService(nService service) {
        nStorage storage =  nStorage.getInstance();
        ArrayList<nService> services = storage.loadServices();
        services.add(service);
        
        storage.saveServices(services);
    }

    /**
     * Remove a service from the storage
     * @param serviceName 
     */
    public static void RemoveService(String serviceName) {
        nStorage storage =  nStorage.getInstance();
        
        // - Remove the Service Configuration
        ArrayList<nService> services = storage.loadServices();
        nService cService = null;
        
        for(nService service : services) {
            if(service.Name.equalsIgnoreCase(serviceName)) {
                cService = service;
                break;
            }
        }
        
        if(cService != null) {
            services.remove(cService);
        }
        
        storage.saveServices(services);
        
        // Clear service state
        nServiceStateTable states = storage.loadStates();
        if(states.containsKey(serviceName.trim())) {
            states.remove(serviceName.trim());
        }
        
        storage.saveStates(states);
    }

    /**
     * Set a property in a service
     * @param propertyName Property name
     * @param propertyValue Property Value
     * @param serviceName Service Name
     * @see .nService 
     */
    public static void SetProperty(String propertyName, String propertyValue, String serviceName) {
        nStorage storage =  nStorage.getInstance();
        
        // - Remove the Service Configuration
        ArrayList<nService> services = storage.loadServices();
        nService cService = null;
        
        for(nService service : services) {
            if(service.Name.equalsIgnoreCase(serviceName)) {
                cService = service;
                break;
            }
        }
        
        if(cService != null) 
        {
            services.remove(cService);
            
            if(propertyName.equalsIgnoreCase("name")) {
                cService.Name = propertyValue.trim();
            } else if(propertyName.equalsIgnoreCase("description")) {
                cService.Description = propertyValue.trim();
            } else if(propertyName.equalsIgnoreCase("refurl")) {
                cService.RefUrl = propertyValue.trim();
            } else if(propertyName.equalsIgnoreCase("Watcher")) {
                cService.Watcher = propertyValue.trim();
            } else if(propertyName.equalsIgnoreCase("RepeatEvery")) {
                cService.RepeatEvery = Integer.valueOf(propertyValue.trim());
            } else {
                if(cService.Properties == null) {
                    cService.Properties = new HashMap<>();
                }
                
                cService.Properties.put(propertyName, propertyValue);
            }
            
            services.add(cService);
        }
        
        storage.saveServices(services);
    }

    /**
     * Remove a property from the service
     * <p>
     * This method will only remove extended properties from the service
     * @param propertyName
     * @param serviceName 
     */
    public static void RemoveProperty(String propertyName, String serviceName) {
        nStorage storage =  nStorage.getInstance();
        
        // - Remove the Service Configuration
        ArrayList<nService> services = storage.loadServices();
        nService cService = null;
        
        for(nService service : services) {
            if(service.Name.equalsIgnoreCase(serviceName)) {
                cService = service;
                break;
            }
        }
        
        if(cService != null) 
        {
            services.remove(cService);
            
            if(propertyName.equalsIgnoreCase("name")) {
            } else if(propertyName.equalsIgnoreCase("description")) {
            } else if(propertyName.equalsIgnoreCase("refurl")) {
            } else if(propertyName.equalsIgnoreCase("Watcher")) {
            } else if(propertyName.equalsIgnoreCase("RepeatEvery")) {
            } else {
                if(cService.Properties != null) {
                    cService.Properties = new HashMap<>();
                }
                
                cService.Properties.remove(propertyName);
            }
            
            services.add(cService);
        }
        
        storage.saveServices(services);
    }
    
    /**
     * List all stored services
     * @return 
     */
    public static ArrayList<nService> AllServices() {
        nStorage storage =  nStorage.getInstance();
        
        // - Remove the Service Configuration
        ArrayList<nService> services = storage.loadServices();
        return services;
    }

    /**
     * Get a service object stored in the main storage
     * @param serviceName The service name
     * @return 
     */
    public static nService GetService(String serviceName) {
        nStorage storage =  nStorage.getInstance();
        
        // - Remove the Service Configuration
        ArrayList<nService> services = storage.loadServices();
        nService cService = null;
        
        for(nService service : services) {
            if(service.Name.equalsIgnoreCase(serviceName)) {
                cService = service;
                break;
            }
        }
        
        return cService;
    }
}
