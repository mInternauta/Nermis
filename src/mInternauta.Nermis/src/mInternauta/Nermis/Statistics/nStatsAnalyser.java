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
package mInternauta.Nermis.Statistics;

import java.io.File;
import java.util.ArrayList;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nStatisticsData;
import static mInternauta.Nermis.Statistics.Native.nStatisticsHelper.Load;

/**
 * A Statistic Analyser 
 */
public class nStatsAnalyser {    
    /**
     * Get the average value of a datasource
     * @param dsName
     * @param service
     * @return 
     */
    public static double average(String dsName, nService service)
    {
        ArrayList<nStatisticsData> data = Load(service);
        
        return calcAverage(data, dsName);
    }   

    /**
     * Sum all values of a datasource
     * @param dsName Datasource Name
     * @param service Service
     * @return 
     */
    public static double sum(String dsName, nService service) {
        ArrayList<nStatisticsData> data = Load(service);
        return calcSum(data, dsName);
    }
    
     /**
     * Get the average value of a datasource
     * @param dsName
     * @return 
     */
    public static double average(File statsFile, String dsName)
    {
        ArrayList<nStatisticsData> data = Load(statsFile);
        
        return calcAverage(data, dsName);
    }   

    /**
     * Sum all values of a datasource
     * @param dsName Datasource Name
     * @return 
     */
    public static double sum(File statsFile, String dsName) {
        ArrayList<nStatisticsData> data = Load(statsFile);
        return calcSum(data, dsName);
    }

    private static double calcAverage(ArrayList<nStatisticsData> data, String dsName) {
        double value = 0;
        
        for(nStatisticsData  stat : data) {
            if(stat.DataSource.equalsIgnoreCase(dsName)) {
                value += stat.Value;
            }
        }
        
        value = value / data.size();
        
        return value;
    }

    private static double calcSum(ArrayList<nStatisticsData> data, String dsName) {
        double value = 0;
        
        for(nStatisticsData  stat : data) {
            if(stat.DataSource.equalsIgnoreCase(dsName)) {
                value += stat.Value;
            }
        }
        
        return value;
    }
}
