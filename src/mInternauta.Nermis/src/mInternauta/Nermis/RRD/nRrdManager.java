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
package mInternauta.Nermis.RRD;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import mInternauta.Nermis.Core.IRrdManager;
import mInternauta.Nermis.Core.nRrdDatasource;
import mInternauta.Nermis.Core.nRrdType;
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

/**
 * Manage all RRD data for the service
 */
public class nRrdManager implements IRrdManager 
{
    /**
     * Update the Rrd data for the Service
     * @param service
     * @param dataSource
     * @param value 
     */
    @Override
    public void UpdateRrd(nService service, String dataSource, double value)
    {
        Date date = new Date();
        
        try {
            File path = nResourceHelper.BuildName("RRD", service.Name);
            RrdDb rrd = new RrdDb(path.getAbsolutePath());
            
            Sample rrdSample = rrd.createSample();
            rrdSample.setTime(date.getTime());
            rrdSample.setValue(dataSource, value);
            rrdSample.update();
                        
            rrd.close();
        } catch (IOException ex) {
             CurrentLogger.log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Creates the Rrd Definition for the service
     * @param service
     * @return 
     * @throws java.io.IOException 
     */
    public RrdDef CreateRrd(nService service) throws IOException
    {
        RrdDef rrd = null;
        File path = nResourceHelper.BuildName("RRD", service.Name);
        
        // - Create the RRD 
        rrd = new RrdDef(path.getAbsolutePath());
        rrd.setVersion(2);
        
        // - Setup the RRD Data Sources
        nServiceWatcher watcher = nController.getWatcherFor(service);        
        for(nRrdDatasource rrdSource : watcher.getRRDDatasources()) {
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
        
        return rrd;
    }

    private static DsType toDsType(nRrdDatasource rrdSource) {
        DsType dsType = DsType.ABSOLUTE;
        if(rrdSource.Type == nRrdType.ABSOLUTE) {
            dsType = DsType.ABSOLUTE;
        }
        if(rrdSource.Type == nRrdType.COUNTER) {
            dsType = DsType.COUNTER;
        }
        if(rrdSource.Type == nRrdType.DERIVE) {
            dsType = DsType.DERIVE;
        }
        if(rrdSource.Type == nRrdType.GAUGE) {
            dsType = DsType.GAUGE;
        }
        return dsType;
    }

}
