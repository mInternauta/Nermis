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
package mInternauta.Nermis.Statistics.RRD;

import mInternauta.Nermis.Core.IStatsGraphManager;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import mInternauta.Nermis.Core.nStatsDatasource;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Persistence.nServiceHelper;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import mInternauta.Nermis.Utils.nResourceHelper;
import mInternauta.Nermis.nController;
import static org.rrd4j.ConsolFun.AVERAGE;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

/**
 * Manage all the Rrd Graph Cache
 */
public class nRrdGraphManager extends IStatsGraphManager {
    public static int IMG_WIDTH = 640;
    public static int IMG_HEIGHT = 128;
    
    private Calendar calendar = Calendar.getInstance();
    
    /**
     * Update the Stats Graphs
     */
    @Override
    public void Update()
    {
        ArrayList<nService> services = nServiceHelper.AllServices();
        CurrentLogger.log(Level.INFO, "Updating the RRD Graphs...");
        
        Date baseDate = new Date();
        
        for(nService service : services) {
            nServiceWatcher watcher = nController.getWatcherFor(service);
            
            for(nStatsDatasource ds : watcher.getStatsDatasources()) {
                genFromCalendar(baseDate, 1, service, ds);
                
                genFromCalendar(baseDate, 4, service, ds);
                
                genFromCalendar(baseDate, 8, service, ds);
                
                genFromCalendar(baseDate, 24, service, ds);
                
                genFromCalendar(baseDate, 48, service, ds);
            }
        }
    }

    private void genFromCalendar(Date baseDate, int hours, nService service, nStatsDatasource ds) {
        calendar.setTime(baseDate);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        long endTime = calendar.getTime().getTime();
        
        this.generate(service, ds, baseDate.getTime(), endTime, String.valueOf(hours) + "hours");
    }
    
    /**
     * Generate a graph 
     * @param service Service
     * @param ds Datasource
     * @param startTime Start Time
     * @param endTime End Time
     * @return  
     */
    public File generate(nService service, nStatsDatasource ds, long startTime, long endTime, String graphName)
    {
        File pathGraph = null;
        
        try {
            pathGraph = getGraphFile(service, ds.InternalName, graphName);
            
            // - Open the database
            File pathDb = nResourceHelper.BuildName("RRD", service.Name);
            
            if(pathDb.exists()) {
                RrdDb rrd = new RrdDb(pathDb.getAbsolutePath());            

                if(pathGraph.exists()) {
                    pathGraph.delete();
                }

                // - Generate the Graph
                RrdGraphDef gDef = new RrdGraphDef();
                gDef.setLocale(Locale.US);
                gDef.setWidth(IMG_WIDTH);
                gDef.setHeight(IMG_HEIGHT);

                // -
                gDef.setFilename(pathGraph.getAbsolutePath());
                gDef.setStartTime(startTime);
                gDef.setEndTime(endTime);
                gDef.setTitle(service.Name + " - " + ds.Name);
                gDef.setVerticalLabel(ds.Name);

                // - Fetch the data
                FetchRequest request = rrd.createFetchRequest(AVERAGE, startTime, endTime);
                
                // - Export the Fetch data
                FetchData data = request.fetchData();
                
                String fetchedData = data.dump();
             
                // - Add the data source
                gDef.datasource(ds.InternalName, ds.InternalName, data);

                // Area
                gDef.area(ds.InternalName, Color.CYAN, ds.Name);

                // Setup the graph
                gDef.setImageInfo("<img src='%s' width='%d' height = '%d'>");
                gDef.setPoolUsed(false);
                gDef.setImageFormat("png");

                // Create the graph
                RrdGraph graph = new RrdGraph(gDef);                                    
            }
        } catch (IOException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
        
        return pathGraph;
    }
}
