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

import java.io.OutputStream;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.logging.Level;
import mInternauta.Nermis.Utils.nResourceHelper;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceStateTable;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;

/**
 * Manages the storage main containers.
 */
public class nStorage {

    private static nStorage instance;
    
    /**
     * Get the instance of nStorage
     * @return 
     */
    public static nStorage newInstance() {
        return new nStorage();
    }
    
    public nSerializerOptions Options;
    private nContainerSerializer serializer;
    
    // - nStorage can have only a single instance, otherwise the cached objects will conflict.
    private nStorage() {        
        this.Options = new nSerializerOptions();
        this.Options.BinaryMode = false;
        this.serializer = new nContainerSerializer();
        this.load();
    }
    
    /**
     * Current Services
     */
    public ArrayList<nService> Services;
    
    /**
     * Current States
     */
    public nServiceStateTable States;
    
    /**
     * Load all stored data
     */
    public void load() {
        this.Services = loadServices();
        this.States = loadStates();
    }
    
    /**
     * Save all storage data
     */
    public void save() {
        saveServices(this.Services);
        saveStates(this.States);
    }
    
    /**
     * Save the container into a resource
     * @param <TContainer> The container type
     * @param container The container
     * @param name
     * @param kind 
     * @see mInternauta.Nermis.Utils.nResourceHelper
     */
    public <TContainer extends nBaseContainer> void saveContainer(TContainer container, String name, String kind) {
        try {
            OutputStream stream = nResourceHelper.WriteResource(name, kind);
            
            serializer.setOptions(Options);
            serializer.Save(container, stream);
            
            stream.flush();
            stream.close();
        } catch (Exception ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Load the container from a resources
     * @param <TContainer> The container type
     * @param name
     * @param kind
     * @return The container object
     * @see mInternauta.Nermis.Utils.nResourceHelper
     */
    public  <TContainer extends nBaseContainer> TContainer loadContainer(String name, String kind)  
    {
        TContainer container = null;
        
        try {
            InputStream stream = nResourceHelper.ReadResource(name, kind);
            
            serializer.setOptions(Options);
            container = serializer.Load(stream);
            
            stream.close();
        } catch (Exception ex) {
            //nController.CurrentLogger.log(Level.SEVERE, null, ex);
        }
        
        return container;
    }
    
    /**
     * Load all services
     * @return 
     */
    public ArrayList<nService> loadServices() {
        ArrayList<nService> services = new ArrayList<>();

        nServiceContainer container = loadContainer("Services", "Settings");
        
        if(container != null) {
            services = container.getMyData();
            if(services == null) {
                services = new ArrayList<>();
            }
        }

        return services;
    }
    
    /**
     * Save the service list
     * @param services 
     */
    public void saveServices(ArrayList<nService> services) 
    {         
        nServiceContainer container =  new nServiceContainer();
        container.setMyData(services);
        
        this.Services = services;
        
        saveContainer(container, "Services", "Settings");
    }
    
    /**
     * Load the state table
     * @return 
     */
    public nServiceStateTable loadStates() {
        nServiceStateTable states = new nServiceStateTable();

        nStatesContainer container = loadContainer("States", "Current");
        
        if(container != null) {
            states = container.getMyData();
            if(states == null) {
                states = new nServiceStateTable();
            }
        }
        
        return states;
    }
    
    /**
     * Save the state table
     * @param states 
     */
    public void saveStates(nServiceStateTable states) 
    {
        nStatesContainer container =  new nStatesContainer();
        container.setMyData(states);
        
        this.States = states;
            
        saveContainer(container, "States", "Current");
    }
}
