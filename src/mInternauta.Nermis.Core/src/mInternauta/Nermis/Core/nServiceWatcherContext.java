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
package mInternauta.Nermis.Core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.StopWatch;

/**
 * Current Watcher Context
 */
public class nServiceWatcherContext {
    private Map<String,Double> StatsData = new HashMap<>();
    private StopWatch MeasurementStopwatch = new StopWatch();    
    
    /**
     * Current Logger
     */
    public Logger Logger;
    
    /**
     * Sync the rrd data
     * @param service
     * @param rrdManager 
     */
    public void UpdateStats(nService service, IStatsDataCollector rrdManager)
    {
        rrdManager.Update(service, this.StatsData);
    }       
    
    /**
     * Set a rrd data
     * @param dsName
     * @param value 
     */
    public void setStatsData(String dsName, double value)
    {
        this.StatsData.put(dsName, value);
    }
    
    /**
     * Stop the RRD Measurement
     * @param dsName 
     */
    public void stopMeasure(String dsName)
    {           
        // Stop the Connection Measurement
        this.MeasurementStopwatch.stop();
            
        // - Update the connection time
        double value = this.MeasurementStopwatch.getTime();
        this.StatsData.put(dsName, value);
        
        Logger.log(Level.INFO, "Updating statistics: {0} to {1}", new Object[]{dsName, String.valueOf(value)});
        
        this.MeasurementStopwatch.reset();
    }
    
    public void beginMeasure()
    {
        // Start the Connection Measurement
        this.MeasurementStopwatch.start();    
    }
}
