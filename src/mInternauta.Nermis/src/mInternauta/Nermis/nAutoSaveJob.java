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

import java.util.logging.Level;
import mInternauta.Nermis.Utils.nApplication;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job to AutoSave all current data in memory to the disk
 */
public class nAutoSaveJob implements Job {

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        CurrentLogger.log(Level.INFO, "Synchronizing all states to the disk...");
        nApplication.XmlStorage.saveStates(nApplication.XmlStorage.States);
    }
    
}
