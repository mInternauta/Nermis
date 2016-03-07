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

import mInternauta.Nermis.Core.IStatsDataCollector;
import mInternauta.Nermis.Core.IStatsGraphManager;
import mInternauta.Nermis.Statistics.Native.nStatsDataCollector;
import mInternauta.Nermis.Statistics.Native.nStatsGraphManager;

/**
 * nStatistics Controller
 */
public class nStatsController {

    private static nStatsGraphManager statsGraphManager;
    private static nStatsDataCollector statsManager;
    
    static {
         // - Builtin Stats Manager
        statsManager = new nStatsDataCollector();
        statsGraphManager = new nStatsGraphManager();     
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
}
