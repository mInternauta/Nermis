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
package mInternauta.Nermis;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mInternauta.Nermis.Persistence.nStorage;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceResults;
import mInternauta.Nermis.Core.nServiceState;
import mInternauta.Nermis.Core.nServiceStateRecord;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Core.nServiceWatcherContext;
import mInternauta.Nermis.Utils.nApplication;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

// TODO: Implement RRD Graphs

/**
 * Watcher Job for the scheduled task
 */
public class nWatcherJob implements Job {

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try 
        {           
            JobDataMap data = jec.getJobDetail().getJobDataMap();       
            
            // - Arguments for the Job
            String watcherName = data.getString("Watcher");
            String serviceName = data.getString("Service");

            Logger logger = nApplication.CreateLogger("Services\\" + serviceName, true);
            
            // -
            CurrentLogger.log(Level.INFO, "Starting job for: {0} - {1}", new Object[]{serviceName, watcherName});
            logger.log(Level.INFO, "Starting job: {0}", jec.getFireInstanceId());

            // -
            logger.log(Level.INFO, "Fetched job information: {0}-{1}", new Object[]{serviceName, watcherName});

            // - Fetch the Watcher and the Service
            nService service = null;
            nServiceWatcher watcher = null;

            //---
            for(nService cService :  nStorage.getInstance().Services) {
                if(cService.Name.equalsIgnoreCase(serviceName)) {
                    service = cService;
                    break;
                }
            }

            //---
            for(nServiceWatcher cWatcher : nController.getRegistredWatchers()) {
                if(cWatcher.getName().equalsIgnoreCase(watcherName)) {
                    watcher = cWatcher;
                    break;
                }
            }

            // - Check the fetched data
            if(service != null && watcher != null) 
            {
                logger.log(Level.INFO, "Executing the Watcher for {0}", serviceName);

                if(watcher.validate(service)) {
                    nServiceWatcherContext watcherContext = new nServiceWatcherContext();
                    watcherContext.Logger = logger;
                    
                    // Executes the Watcher
                    nServiceResults results = watcher.execute(service, watcherContext);

                    // Save the Result in the States Table
                    nServiceStateRecord record = new nServiceStateRecord();
                    record.State = results.State;
                    record.UpdatedAt = new Date();

                     nStorage.getInstance().States.put(serviceName, record);
                     nStorage.getInstance().saveStates(nStorage.getInstance().States);

                     logger.log(Level.INFO, "Executed the Watcher for {0}", serviceName);
                     
                     if(record.State != nServiceState.ONLINE) {
                         String message = "Unresponsive";          
                         
                         if(results.Message != null && results.Message.isEmpty() == false) {
                             message = results.Message;
                         }
                         
                         nApplication.getNofitier().NotifyServiceOffine(service, message);
                         watcherContext.setStatsData("fails", 1);
                     }
                     else 
                     {
                         watcherContext.setStatsData("success", 1);
                     }
                     
                     
                     // Save the Rrd Data
                     watcherContext.UpdateStats(service, nController.getStatsManager());
                } else {
                    logger.log(Level.SEVERE, "Invalid service settings for the current watcher: {0}", serviceName);
                }
            }
            else 
            {
                logger.log(Level.SEVERE, "Cant fetch service information: {0}", serviceName);
            }
        } 
        catch(Exception ex)
        {
            CurrentLogger.log(Level.INFO, "Error in Watcher Job: {0}", ex.toString());
        }
    }
    
}
