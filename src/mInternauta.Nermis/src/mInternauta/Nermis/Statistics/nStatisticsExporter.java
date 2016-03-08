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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import mInternauta.Nermis.Core.IStatsGraphManager;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nStatisticsData;
import mInternauta.Nermis.Core.nStatsGraphDef;
import mInternauta.Nermis.Persistence.nServiceHelper;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import mInternauta.Nermis.Utils.nResourceHelper;
import mInternauta.Nermis.nController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Export any statistics data to a file
 */
public class nStatisticsExporter {    
    
    /**
     * Export all services statistics to a package
     *
     * @param toPackage
     */
    public void ExportAll(File toPackage) {
        try {
            ZipOutputStream outputZip = new ZipOutputStream(new FileOutputStream(toPackage));
            
            // Added the Directories 
            outputZip.putNextEntry(new ZipEntry("Graphs/"));
            outputZip.putNextEntry(new ZipEntry("Statistics/"));

            // Export all data
            for (nService service : nServiceHelper.AllServices()) {
                exportStatistics(service, outputZip);
                
                exportGraphs(service, outputZip);
            }
            
            outputZip.flush();
            outputZip.close();
        } catch (FileNotFoundException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Export all statistics data for the service to a file
     *
     * @param service
     * @param to
     */
    public void Export(nService service, File to) {
        nStatsAnalyser analyser = new nStatsAnalyser();
        
        try {
            ArrayList<nStatisticsData> stats = nStatsController
                    .getStatsManager()
                    .Load(service);

            String description = service.Description;
            String refUrl = service.RefUrl;
            
            ExportData(to, description, refUrl, stats);
        } catch (FileNotFoundException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }   

    /**
     * Export the statistics collection to the file
     * @param to
     * @param description
     * @param refUrl
     * @param stats
     * @throws IOException
     * @throws FileNotFoundException 
     */
    public void ExportData(File to, String description, String refUrl, ArrayList<nStatisticsData> stats) 
            throws IOException, FileNotFoundException 
    {
        // Clean ou create the file
        if (to.exists()) {
            to.delete();
        }
        
        // - Export all data to the file
        FileOutputStream stream = new FileOutputStream(to);
        
        IOUtils.write("Nermis - Exported Statistics", stream);
        IOUtils.write("\r\nService: " + description, stream);
        IOUtils.write("\r\nUrl: " + refUrl, stream);
        
        IOUtils.write("\r\n", stream);
        IOUtils.write("\r\n", stream);
        
        // Sum all fails and all success
        IOUtils.write("\r\nFails: " + String.valueOf(sum(stats, "fails")), stream);
        IOUtils.write("\r\nSuccess: " + String.valueOf(sum(stats, "success")), stream);
        
        IOUtils.write("\r\n", stream);
        IOUtils.write("\r\n", stream);
        
        // Write all data
        IOUtils.write("\r\n|- Name            |- Date            |- Value            |", stream);
        for (nStatisticsData stat : stats) {
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
    }
    
    private double sum(ArrayList<nStatisticsData> data, String dsName) {
        double value = 0;
        
        for(nStatisticsData stat : data) {
            if(stat.DataSource.equalsIgnoreCase(dsName)) {
                value = stat.Value;
            }
        }
        
        return value;
    }

    private void exportGraphs(nService service, ZipOutputStream outputZip) throws IOException {
        // Export all Graphics
        ArrayList<nStatsGraphDef> graphs = IStatsGraphManager.getAllGraphs(service);
        
        for(nStatsGraphDef graph : graphs) {
            File grpFile = new File(graph.GraphFile);
            
            if(grpFile.exists()) {
                String name = grpFile.getName();
                outputZip.putNextEntry(new ZipEntry("Graphs/" + name));
                IOUtils.copy(new FileInputStream(grpFile), outputZip);
                outputZip.closeEntry();
            }
        }
    }

    private void exportStatistics(nService service, ZipOutputStream outputZip) throws IOException {
        File tempFile = nResourceHelper.BuildName("Temp", service.Name + UUID.randomUUID().toString());
        this.Export(service, tempFile);
        
        if(tempFile.exists()) {
            outputZip.putNextEntry(new ZipEntry("Statistics/" + service.Name + ".txt"));
            IOUtils.copy(new FileInputStream(tempFile), outputZip);
            outputZip.closeEntry();
        }
    }
}
