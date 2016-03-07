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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Core.IStatsGraphManager;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Core.nStatsDatasource;
import mInternauta.Nermis.Core.nStatsGraphDef;
import mInternauta.Nermis.Persistence.nServiceHelper;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import mInternauta.Nermis.nController;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Manage the Charts
 */
public class nStatsGraphManager extends IStatsGraphManager {

    @Override
    public void Update() {
        for (nService service : nServiceHelper.AllServices()) {
            // Load all data 
            ArrayList<nStatisticsData> data = nStatisticsHelper.Load(service);

            if (data.size() > 0) {
                nServiceWatcher watcher = nController.getWatcherFor(service);
                
                for (nStatsDatasource dSource : watcher.getStatsDatasources()) {
                    if (dSource.InternalName.equalsIgnoreCase("fails") == false
                            && dSource.InternalName.equalsIgnoreCase("success") == false)  // Ignore Global Datasources
                    {
                        generateWatcherGraphics(data, service, dSource);
                    }
                }
                
                // Generate the Global Fails and Success Chart
                generateFailsSucessGraphics(service, data);
            }
        }
    }

    private void generateWatcherGraphics(ArrayList<nStatisticsData> data, nService service, nStatsDatasource dSource) {
        // Generate a Graph for the last hour
        generateWatcherChart(data, service, dSource.InternalName, 1);

        // Generate a Graph for the 4 hours
        generateWatcherChart(data, service, dSource.InternalName, 4);

        // Generate a Graph for the 12 hours
        generateWatcherChart(data, service, dSource.InternalName, 12);

        // Generate a Graph for the 24 hours
        generateWatcherChart(data, service, dSource.InternalName, 24);

        // Generate a Graph for the 48 hours
        generateWatcherChart(data, service, dSource.InternalName, 48);
    }

    private void generateWatcherChart(ArrayList<nStatisticsData> data,
            nService service,
            String dsName,
            int lastHours) {

        String graphName = String.valueOf(lastHours) + "hours";
        Date baseDate = new Date();

        Date startDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(lastHours));
        Date endDate = baseDate;

        File chartFile = this.getGraphFile(service, dsName, graphName);

        // Load the header
        nStatisticsHeader header = nStatisticsHelper.LoadHeader(service, dsName);

        if (data.size() > 0) {
            try {
                // Build the data
                DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();

                // Fill the data
                for (nStatisticsData stat : data) {
                    if (this.isValidStats(stat, startDate, endDate, dsName)) {
                        chartDataset.addValue((Number) stat.Value, stat.DataSource, toChartDate(stat.Time));
                    }
                }

                if (chartDataset.getRowCount() > 0) {
                    // Build the chart
                    String charTitle = nConfigHelper.getDisplayLanguage().getProperty("CHART_LASTHOURS");
                    charTitle = charTitle.replace("{0}", String.valueOf(lastHours));

                    JFreeChart chartObject = ChartFactory.createLineChart3D(charTitle, service.Description + " - " + header.Description,
                            header.Description, chartDataset, PlotOrientation.VERTICAL, true, true, true);

                    ChartUtilities.saveChartAsPNG(chartFile, chartObject, 1280, 384);

                    nStatsGraphDef graphDef = new nStatsGraphDef();
                    graphDef.Datasource = header.Description;
                    graphDef.GeneratedAt = new Date().getTime();
                    graphDef.GraphFile = chartFile.getAbsolutePath();
                    graphDef.Name = graphName;
                    graphDef.ServiceName = service.Name;

                    this.saveGraphDef(graphDef);
                }
            } catch (IOException ex) {
                CurrentLogger.log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isValidStats(nStatisticsData stat, Date startDate, Date endDate, String dsName) {
        Date statTime = new Date(stat.Time);

        if (stat.DataSource.equalsIgnoreCase(dsName)) {
            if (startDate.before(statTime) && endDate.after(statTime)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private Comparable toChartDate(long Time) {
        Date date = new Date(Time);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        return format.format(date);
    }

    private void generateFailsSucessGraphics(nService service, ArrayList<nStatisticsData> data) {
        try {
            // Sum all Fails and All Success data
            long fails = 0;
            long success = 0;
            
            // -
            for(nStatisticsData stat : data) {
                if(stat.DataSource.equalsIgnoreCase("fails")) {
                    fails += stat.Value;
                }
                
                if(stat.DataSource.equalsIgnoreCase("success")) {
                    success += stat.Value;
                }
            }
            
            // - Generate the chart
            DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();
            chartDataset.addValue(fails, service.Name, "fails");
            chartDataset.addValue(success, service.Name, "success");
            
            // -
            String graphName = "FailsAndSucess";
            File chartFile = this.getGraphFile(service, "fails_success", graphName);
            
            String charTitle = nConfigHelper.getDisplayLanguage().getProperty("CHAR_FAILSANDSUCESS");
            
            JFreeChart chartObject = ChartFactory.createBarChart(charTitle, service.Description + " - " + charTitle,
                    charTitle, chartDataset, PlotOrientation.VERTICAL, true, true, true);
            
            ChartUtilities.saveChartAsPNG(chartFile, chartObject, 1280, 384);
            
            nStatsGraphDef graphDef = new nStatsGraphDef();
            graphDef.Datasource = "fails&success";
            graphDef.GeneratedAt = new Date().getTime();
            graphDef.GraphFile = chartFile.getAbsolutePath();
            graphDef.Name = graphName;
            graphDef.ServiceName = service.Name;
            
            this.saveGraphDef(graphDef);
        } catch (IOException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }
}
 