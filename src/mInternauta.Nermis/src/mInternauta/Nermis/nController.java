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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Level;
import mInternauta.Nermis.Persistence.nStorage;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Configs.nConfiguration;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import static mInternauta.Nermis.Utils.nApplication.Notifiers;
import static mInternauta.Nermis.Utils.nApplication.Watchers;
import mInternauta.Nermis.Utils.nJarManager;
import mInternauta.Nermis.Web.nWebServer;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import mInternauta.Nermis.Core.IStatsGraphManager;
import mInternauta.Nermis.Core.IStatsDataCollector;
import mInternauta.Nermis.Statistics.nStatsDataCollector;
import mInternauta.Nermis.Statistics.nStatsGraphManager;

/**
 * Nermis Main Controller
 * <p>
 * This object performs the control of all Nermis functionality  in server / service mode
 */
public class nController {         
    private Scheduler scheduler;    
    private nWebServer webServer;
    
    private static IStatsDataCollector statsManager;
    private static IStatsGraphManager statsGraphManager;
    
    static {
        // Load all Watchers
        CurrentLogger.log(Level.INFO, "Registering watchers... ");
        Watchers = new ArrayList<>();
        
        nConfiguration cfg = nConfigHelper.getConfiguration();
        _checkJarsConfig(cfg);
        
        // Load all configured watchers and notifiers
        for(Entry<String, String> entry : cfg.IncludedJars.entrySet()) {
            CurrentLogger.log(Level.INFO, "Loading: {0}", entry.getKey());
            Watchers.addAll(nJarManager.LoadWatchersFromJar(entry.getValue()));
            Notifiers.addAll(nJarManager.LoadNotifiersFromJar(entry.getValue()));
        }
        
        // - Builtin Stats Manager
        statsManager = new nStatsDataCollector();
        statsGraphManager = new nStatsGraphManager();        
    }

    private static void _checkJarsConfig(nConfiguration cfg) {                
        if(cfg.IncludedJars == null || cfg.IncludedJars.isEmpty()) {
            nConfiguration cfg2 = nConfigHelper.getDefaults();
            cfg.IncludedJars = cfg2.IncludedJars;
            nConfigHelper.Save(cfg);
        }
    }
    
    /**
     * Return the Statistics Data Manager instance 
     * @return 
     */
    public static IStatsDataCollector getStatsManager() {
        return statsManager;
    }
    
      /**
     * Return the Statistics Graph Manager instance 
     * @return 
     */
    public static IStatsGraphManager getStatsGraphManager() {
        return statsGraphManager;
    }
    
    /**
     * Search for the watcher of the service
     * @param service
     * @return the watcher or null if cant find the watcher
     */
    public static nServiceWatcher getWatcherFor(nService service) {
        nServiceWatcher watcher = null;
        for (nServiceWatcher cWatcher : nController.getRegistredWatchers()) {
            if (cWatcher.getName().equalsIgnoreCase(service.Watcher)) {
                watcher = cWatcher;
                break;
            }
        }
        return watcher;
    }
    
    /**
     * This method returns the current list of Service Watchers
     * @return 
     */
    public static ArrayList<nServiceWatcher> getRegistredWatchers() {
        return Watchers;
    }
    /**
     * Start the controller 
     */
    public void Start() {
        try {            
            // -
            CurrentLogger.log(Level.INFO, "Nermis Remote Service Monitoring Controller");
            CurrentLogger.log(Level.INFO, "Copyright (C) 2016");
            CurrentLogger.log(Level.INFO, "Loading... ");
            
            this.webServer = new nWebServer();
                       
            // - Setup the Scheduler
            CurrentLogger.log(Level.INFO, "Configuring all schedulers... ");
            SchedulerFactory sdf = new StdSchedulerFactory();
            this.scheduler = sdf.getScheduler();
            
            // -
            loadAllJobs();
                 
            // -
            setupStats();
            
            // - Start all Jobs
            scheduler.startDelayed(5);
            
            // - Start the Web Server
            this.webServer.Start();
            
            // - 
            CurrentLogger.log(Level.INFO, "Loaded");
        } catch (SecurityException | SchedulerException | IOException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }

    private void setupStats() throws IOException, SchedulerException {
        // -  Load Stats Graphs
        CurrentLogger.log(Level.INFO, "Configuring all statistics data... ");
        
        // - Rrd Data Manager (Disabled, cant make work now)        
        // * statsManager = new nRrdManager();
               
        for(nService service :  nStorage.getInstance().Services) {
            statsManager.Create(service);
        }
        
        setupStatsJob();
    }

    private void setupStatsJob() throws SchedulerException {
        // - Setup Rrd Job
        JobDetail job = JobBuilder.newJob(nStatsGraphsJob.class)
                .withIdentity("Job_StatsGraphs", "Stats")
                .build();
        
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(80)
                .repeatForever();
        
        // - Build the Trigger
        SimpleTrigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("Tg_StatsGraphs", "Stats")
                .startAt(new Date())
                .withSchedule(schedule)
                .build();
        
        // -
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * Stops the controller and the job scheduler
     */
    public void Stop() 
    {
        CurrentLogger.log(Level.INFO, "Stopping..");
        
        // - Shutdown the storage
        if(scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException ex) {
                CurrentLogger.log(Level.SEVERE, null, ex);
            }
        }
        
        // - Stop the webserver
        this.webServer.Stop();
        
        // - Save all data        
         nStorage.getInstance().save();
    }
    
    private void loadAllJobs() throws SchedulerException {
        // - Setup all Jobs
        for(nService service :  nStorage.getInstance().Services) {
            CurrentLogger.log(Level.INFO, "Configuring {0}", service.Name);
            
            // - Build the Job
            JobDetail job = JobBuilder.newJob(nWatcherJob.class)
                    .withIdentity("Job_" + service.Name, "Services")
                    .build();
            
            // - Store the data
            job.getJobDataMap().put("Watcher", service.Watcher);
            job.getJobDataMap().put("Service", service.Name);
            
            // - Schedule
            SimpleScheduleBuilder schedule;
            
            if(service.RepeatEvery > 0) {
                schedule = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(service.RepeatEvery)
                        .repeatForever();
            }
            else
            {
                schedule = SimpleScheduleBuilder.simpleSchedule();
            }
            
            // - Build the Trigger
            SimpleTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("Tg_" + service.Name, "Services")
                    .startAt(new Date())
                    .withSchedule(schedule)
                    .build();
            
            // -
            scheduler.scheduleJob(job, trigger);
        }
    }
}
