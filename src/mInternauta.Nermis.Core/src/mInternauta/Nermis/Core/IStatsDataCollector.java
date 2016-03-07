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

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Interface for the Rrd Manager
 */
public interface IStatsDataCollector {
    /**
     * Update a collection of Statistics data
     * @param service
     * @param values 
     */
    public void Update(nService service, Map<String, Double> values);
    
    /**
     * Create the Statistics data for the service
     * @param service
     * @return 
     */
    public boolean Create(nService service);

    /**
     * Load all statistics data for a service
     * @param service
     * @return 
     */
    public ArrayList<nStatisticsData> Load(nService service);
    
    /**
     * Load all statistics data from a file
     * @return 
     */
    public ArrayList<nStatisticsData> Load(File dataFile);
}
