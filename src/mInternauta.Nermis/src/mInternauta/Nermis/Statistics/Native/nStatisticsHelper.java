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
package mInternauta.Nermis.Statistics.Native;

import mInternauta.Nermis.Core.nStatisticsData;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Persistence.nStorage;
import mInternauta.Nermis.Utils.nApplication;
import mInternauta.Nermis.Utils.nResourceHelper;

/**
 * Helps Load and Save a Service Statistics
 */
public class nStatisticsHelper {

    /**
     * Save the service statistics header
     * @param service
     * @param header 
     */
    public static void SaveHeader(nService service, nStatisticsHeader header)
    {
        nStorage storage = nApplication.BinaryStorage;
        
        nStatsHeaderContainer container = null;
        
        ArrayList<nStatisticsHeader> data = new ArrayList<>();
        File storagePath = nResourceHelper.BuildName("Statistics", "Header-" + service.Name);

        if (storagePath.exists()) {
            container = storage.loadContainer("Header-" + service.Name, "Statistics");
            data = container.getMyData();
        }
        else 
        {
            container = new nStatsHeaderContainer();
        }
        
        nStatisticsHeader cHeader = null;
        for(nStatisticsHeader cIHeader : data) {
            if(cIHeader.Datasource.equalsIgnoreCase(header.Datasource)) {
                cHeader = cIHeader;
                break;
            }
        }
        
        if(cHeader != null) {
            data.remove(cHeader);
        }
        
        data.add(header);
        container.setMyData(data);
        storage.saveContainer(container, "Header-" + service.Name, "Statistics");
    }
    
    /**
     * Load the service statistics header
     * @param service
     * @return 
     */
    public static nStatisticsHeader LoadHeader(nService service, String dsName)
    {
        ArrayList<nStatisticsHeader> data = new ArrayList<>();
        File storagePath = nResourceHelper.BuildName("Statistics", "Header-" + service.Name);

        if (storagePath.exists()) {
            nStorage storage = nApplication.BinaryStorage;
            nStatsHeaderContainer container = storage.loadContainer("Header-" + service.Name, "Statistics");
            data = container.getMyData();
        }
        
        nStatisticsHeader cHeader = null;
        for(nStatisticsHeader cIHeader : data) {
            if(cIHeader.Datasource.equalsIgnoreCase(dsName)) {
                cHeader = cIHeader;
                break;
            }
        }
        
        return cHeader;
    }
    
    /**
     * Load the statistics for the service
     *
     * @param service
     * @return
     */
    public static ArrayList<nStatisticsData> Load(nService service) {
        ArrayList<nStatisticsData> data = new ArrayList<>();
        File storagePath = nResourceHelper.BuildName("Statistics", service.Name);

        if (storagePath.exists()) {
            nStorage storage = nApplication.BinaryStorage;
            nStatsDataContainer container = storage.loadContainer(service.Name, "Statistics");
            data = container.getMyData();
        }
        
        return data;
    }
    
    /**
     * Update a statistics value for the service
     * @param service
     * @param dsName
     * @param value 
     */
    public static void Update(nService service, String dsName, double value)
    {        
        nStorage storage = nApplication.BinaryStorage;
        nStatsDataContainer container = null;
        
        ArrayList<nStatisticsData> data = new ArrayList<>();
        File storagePath = nResourceHelper.BuildName("Statistics", service.Name);

        if (storagePath.exists()) {
            container = storage.loadContainer(service.Name, "Statistics");
            data = container.getMyData();
        }
        else 
        {
            container = new nStatsDataContainer();
        }
        
        nStatisticsHeader ds = LoadHeader(service, dsName);
        
        if(ds != null) 
        {
            boolean isValid = value <= ds.MaxValue && value >= ds.MinValue;
            
            if(isValid) {            
                Date currentDate = new Date();
                nStatisticsData newData = new nStatisticsData();
                newData.DataSource = dsName;
                newData.Value = value;
                newData.Time = currentDate.getTime();

                data.add(newData);

                container.setMyData(data);
                storage.saveContainer(container, service.Name, "Statistics");
            }
        }
    }

    /**
     * Sum all values of a datasource
     * @param dsName Datasource Name
     * @param service Service
     * @return 
     */
    public static double sum(String dsName, nService service) {
        ArrayList<nStatisticsData> data = Load(service);
        double value = 0;
        
        for(nStatisticsData  stat : data) {
            if(stat.DataSource.equalsIgnoreCase(dsName)) {
                value += stat.Value;
            }
        }
        
        return value;
    }
}
