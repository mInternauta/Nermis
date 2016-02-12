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

import mInternauta.Nermis.Utils.nResourceHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import mInternauta.Nermis.Persistence.nStorage;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Configs.nConfiguration;
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

/**
 * Nermis Main Controller
 * <p>
 * This object performs the control of all Nermis functionality  in server / service mode
 */
public class nController {    
    /**
     * Global logger for the Nermis 
     */
    public static final Logger CurrentLogger = Logger.getLogger("NermisLogger");        
       
    private static ArrayList<nServiceWatcher> Watchers;           
    private Scheduler scheduler;    
    
    static {
        // Load all Watchers
        CurrentLogger.log(Level.INFO, "Registering watchers... ");
        Watchers = new ArrayList<>();
        
        nConfiguration cfg = nConfigHelper.getConfiguration();
        _checkWatchersConfig(cfg);
        
        // Load all configured watchers
        for(Entry<String, String> entry : cfg.WatchersJars.entrySet()) {
            CurrentLogger.log(Level.INFO, "Loading: " + entry.getKey());
            Watchers.addAll(nJarManager.LoadWatchersFromJar(entry.getValue()));
        }
    }

    private static void _checkWatchersConfig(nConfiguration cfg) {                
        if(cfg.WatchersJars == null || cfg.WatchersJars.isEmpty()) {
            nConfiguration cfg2 = nConfigHelper.getDefaults();
            cfg.WatchersJars = cfg2.WatchersJars;
            nConfigHelper.Save(cfg);
        }
    }
    
    /**
     * This method returns the current list of Service Watchers
     * @return 
     */
    public static ArrayList<nServiceWatcher> getRegistredWatchers() {
        return Watchers;
    }
    private nWebServer webServer;
    
    /**
     * Start the controller 
     */
    public void Start() {
        try {
            // Setup the Logger
            CurrentLogger.setLevel(Level.ALL);
            CurrentLogger.setUseParentHandlers(false);
            CurrentLogger.addHandler(new ConsoleHandler());
            FileHandler fileLog = new FileHandler(nResourceHelper.BuildName("Logs", "Global").getAbsolutePath(), 1000024, 5);
            fileLog.setFormatter(new SimpleFormatter());
            CurrentLogger.addHandler(fileLog);
            
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
            scheduler.startDelayed(5);
            
            // - Start the Web Server
            this.webServer.Start();
            
            // - 
            CurrentLogger.log(Level.INFO, "Loaded");
        } catch (IOException | SecurityException | SchedulerException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
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
