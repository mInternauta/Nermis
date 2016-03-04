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
package mInternauta.Nermis.Statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.IStatsDataCollector;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Core.nStatsDataType;
import mInternauta.Nermis.Core.nStatsDatasource;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import mInternauta.Nermis.nController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Generic Statistics Data Collector
 */
public class nStatsDataCollector implements IStatsDataCollector {

    @Override
    public void Update(nService service, Map<String, Double> values) {
        for(Entry<String, Double> value : values.entrySet()) {
            nStatisticsHelper.Update(service, value.getKey(), value.getValue());
        }
    }

    @Override
    public boolean Create(nService service) {
        boolean isCreated = false;
        
         // - Setup the  Data Sources
        nServiceWatcher watcher = nController.getWatcherFor(service);
        for(nStatsDatasource dSource : watcher.getStatsDatasources()) {
           nStatisticsHeader header = new nStatisticsHeader();
           header.Datasource = dSource.InternalName;
           header.Description = dSource.Name;
           header.MaxValue = dSource.MaxValue;
           header.MinValue = dSource.MinValue;
           header.Type = dSource.Type;
           
           nStatisticsHelper.SaveHeader(service, header);
        }
                
        addSuccessHeader(service);
        addFailsHeader(service);
           
        isCreated = true;
        
        return isCreated;
    }

    private void addSuccessHeader(nService service) {
        nStatisticsHeader header = new nStatisticsHeader();
        header.Datasource = "success";
        header.Description = "success";
        header.MaxValue = Double.MAX_VALUE;
        header.MinValue = 0;
        header.Type = nStatsDataType.COUNTER;
        
        nStatisticsHelper.SaveHeader(service, header);
    }
    
    private void addFailsHeader(nService service) {
        nStatisticsHeader header = new nStatisticsHeader();
        header.Datasource = "fails";
        header.Description = "fails";
        header.MaxValue = Double.MAX_VALUE;
        header.MinValue = 0;
        header.Type = nStatsDataType.COUNTER;
        
        nStatisticsHelper.SaveHeader(service, header);
    } 

    @Override
    public void Export(nService service, File to) {
        try {
            ArrayList<nStatisticsData> stats = nStatisticsHelper.Load(service);
            
            // Clean ou create the file
            if(to.exists()) {
                to.delete();
            }  
            
            // - Export all data to the file
            FileOutputStream stream = new FileOutputStream(to);
            
            IOUtils.write("Nermis - Exported Statistics", stream);
            IOUtils.write("\r\nService: " + service.Description, stream);
            IOUtils.write("\r\nUrl: " + service.RefUrl, stream);
            
            IOUtils.write("\r\n", stream);
            IOUtils.write("\r\n", stream);
            
            // Sum all fails and all success
            IOUtils.write("\r\nFails: " + String.valueOf(nStatisticsHelper.sum("fails", service)), stream);
            IOUtils.write("\r\nSuccess: " + String.valueOf(nStatisticsHelper.sum("success", service)), stream);
            
            IOUtils.write("\r\n", stream);
            IOUtils.write("\r\n", stream);
            
            // Write all data 
            IOUtils.write("\r\n|- Name             |- Date             |- Value              |", stream);
            for(nStatisticsData stat : stats) {
                String name = StringUtils.rightPad(stat.DataSource, 17);
                String value = StringUtils.rightPad(String.valueOf(stat.Value), 17);
                
                Date date = new Date(stat.Time);
                SimpleDateFormat format = new SimpleDateFormat();
                String cDate = StringUtils.rightPad(format.format(date), 17);
                
                IOUtils.write("\r\n| " + name + "| " + value + "| " + cDate + " |", stream);
            }
            
            IOUtils.write("\r\n", stream);
            IOUtils.write("\r\n", stream);
            
            stream.close();
        } catch (FileNotFoundException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }       
    }
}
