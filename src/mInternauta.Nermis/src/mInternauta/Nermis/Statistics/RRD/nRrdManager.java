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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import mInternauta.Nermis.Core.nStatsDatasource;
import mInternauta.Nermis.Core.nStatsDataType;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceWatcher;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import mInternauta.Nermis.Utils.nResourceHelper;
import mInternauta.Nermis.nController;
import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.ConsolFun.MAX;
import static org.rrd4j.ConsolFun.TOTAL;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import mInternauta.Nermis.Core.IStatsDataCollector;
import mInternauta.Nermis.Core.nStatisticsData;

/**
 * Manage all RRD data for the service
 */
public class nRrdManager implements IStatsDataCollector 
{
    /**
     * Update the Rrd data for the Service
     * @param service Service
     * @param values Values
     */
    @Override
    public void Update(nService service, Map<String, Double> values)
    {        
        try {
            File path = nResourceHelper.BuildName("RRD", service.Name);
            RrdDb rrd = new RrdDb(path.getAbsolutePath());
            
            Sample rrdSample = rrd.createSample();
            
            for(Entry<String,Double> entry : values.entrySet()) {                
                rrdSample.setValue(entry.getKey(), entry.getValue());
            }
            
            rrdSample.update();
            
            String xml = rrd.getXml();

            rrd.close();
        } catch (IOException ex) {
             CurrentLogger.log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Creates the Rrd Definition for the service
     * @param service
     * @return 
     */
    public boolean Create(nService service) 
    {        
        RrdDef rrd = null;
            
        try {
            File path = nResourceHelper.BuildName("RRD", service.Name);
            
            // - Create the RRD
            rrd = new RrdDef(path.getAbsolutePath());
            rrd.setVersion(2);
            
            // - Setup the RRD Data Sources
            nServiceWatcher watcher = nController.getWatcherFor(service);
            for(nStatsDatasource rrdSource : watcher.getStatsDatasources()) {
                DsType dsType = toDsType(rrdSource);
                rrd.addDatasource(rrdSource.InternalName, dsType,
                        rrdSource.Heartbeat,
                        rrdSource.MinValue,
                        rrdSource.MaxValue);
            }
            
            // - Global Datasources
            rrd.addDatasource("fails", DsType.COUNTER, 300, 0, Double.MAX_VALUE);
            rrd.addDatasource("success", DsType.COUNTER, 300, 0, Double.MAX_VALUE);
            
            // - Add all archives
            rrd.addArchive(AVERAGE, 0.5, 1, 600);
            rrd.addArchive(AVERAGE, 0.5, 6, 700);
            rrd.addArchive(AVERAGE, 0.5, 24, 775);
            rrd.addArchive(AVERAGE, 0.5, 288, 797);
            rrd.addArchive(TOTAL, 0.5, 1, 600);
            rrd.addArchive(TOTAL, 0.5, 6, 700);
            rrd.addArchive(TOTAL, 0.5, 24, 775);
            rrd.addArchive(TOTAL, 0.5, 288, 797);
            rrd.addArchive(MAX, 0.5, 1, 600);
            rrd.addArchive(MAX, 0.5, 6, 700);
            rrd.addArchive(MAX, 0.5, 24, 775);
            rrd.addArchive(MAX, 0.5, 288, 797);
            
            // - Create the rrd file
            RrdDb rrdDb = new RrdDb(rrd);
            rrdDb.close();
        } catch (IOException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }       

        return rrd != null;
    }

    private static DsType toDsType(nStatsDatasource rrdSource) {
        DsType dsType = DsType.ABSOLUTE;
        if(rrdSource.Type == nStatsDataType.ABSOLUTE) {
            dsType = DsType.ABSOLUTE;
        }
        if(rrdSource.Type == nStatsDataType.COUNTER) {
            dsType = DsType.COUNTER;
        }
        if(rrdSource.Type == nStatsDataType.DERIVE) {
            dsType = DsType.DERIVE;
        }
        if(rrdSource.Type == nStatsDataType.GAUGE) {
            dsType = DsType.GAUGE;
        }
        return dsType;
    }

    @Override
    public ArrayList<nStatisticsData> Load(nService service) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
