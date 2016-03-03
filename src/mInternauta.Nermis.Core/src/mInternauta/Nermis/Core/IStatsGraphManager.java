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
package mInternauta.Nermis.Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import mInternauta.Nermis.Utils.nResourceHelper;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

/**
 * Manage all Stats Graphs
 */
public abstract class IStatsGraphManager {

    /**
     * Update the Stats Graphs
     */
    public abstract void Update();
    
    /**
     * Get path to the generate graph
     * @param service
     * @param ds
     * @param graphName
     * @return 
     */
    public File getGraphFile(nService service, String dsName, String graphName)
    {
        File pathGraph;
        pathGraph = nResourceHelper.BuildName("Graphs", service.Name + "_" + dsName + "_" + graphName);
        return pathGraph;
    }

    public static ArrayList<nStatsGraphDef> getAllGraphs(nService service)
    {
        ArrayList<nStatsGraphDef> graphs = new ArrayList<nStatsGraphDef>();
        
        String gName = "GraphData-" + service.Name;
        
        // - Search for Definitions
        File dir = new File(nResourceHelper.BuildDirectory("Current"));
        File[] graphDefs = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(gName);
            }
        });
        
        // - Load all Graphs
        for(File gFile : graphDefs) {
            nStatsGraphDef def = loadGraphDef(gFile);
            
            if(def.ServiceName.equalsIgnoreCase(service.Name)) {
                graphs.add(def);
            }
        }
        
        return graphs;
    }
    
    public static nStatsGraphDef loadGraphDef(File gFile) {
        JBossObjectInputStream o = null;        
        nStatsGraphDef def = new nStatsGraphDef();
            
        try {
            o = new JBossObjectInputStream(new FileInputStream(gFile));
            def = (nStatsGraphDef)o.readObject();
            o.close();
        } catch (IOException | ClassNotFoundException ex) { 
             CurrentLogger.log(Level.SEVERE, null, ex);
        }
                
        return def;
    }
    
    protected void saveGraphDef(nStatsGraphDef graph) {
        try 
        {
            OutputStream stream = nResourceHelper.WriteResource("GraphData-" + graph.ServiceName + graph.Name, "Current");
            JBossObjectOutputStream o = new JBossObjectOutputStream(stream);
            o.writeObject(graph);
            o.close();
        } catch (Exception ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }
}
