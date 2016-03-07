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
package minternauta.nermis.deamon.CLI;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Persistence.nServiceHelper;
import mInternauta.Nermis.Statistics.nStatisticsExporter;
import mInternauta.Nermis.Utils.nResourceHelper;
import mInternauta.Nermis.nController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Statistics Commands for CLI
 */
public class CLIStatistics implements ICLICommand {

    @Override
    public String getName() {
        return "statistics";
    }

    @Override
    public Options getOptions() {
        Options options = new Options();
        options.addOption(new Option("help", "Display command help"));

        options.addOption(
                Option.builder("exportstats")
                .desc("Export the statistics for a service")
                .hasArg()
                .build());

        options.addOption(
                Option.builder("exportall")
                .desc("Export all the statistics to a package")
                .build());

        return options;
    }

    @Override
    public void Execute(CommandLine cmd) {
        exportstats(cmd);
        exportall(cmd);
    }

    private void exportall(CommandLine cmd) {
        nStatisticsExporter exporter = new nStatisticsExporter();

        if (cmd.hasOption("exportall")) {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd-HH_mm");

            File exportFile = nResourceHelper.BuildName("Exported", "Statistics" + format.format(now) + ".zip");
            System.out.println("Exporting all statistics to: " + exportFile.getAbsolutePath());
            exporter.ExportAll(exportFile);
            System.out.println("Exported");
        }
    }

    private void exportstats(CommandLine cmd) {
        nStatisticsExporter exporter = new nStatisticsExporter();

        // Export service statistics
        if (cmd.hasOption("exportstats")) {
            String serviceName = cmd.getOptionValue("exportstats");

            if (serviceName != null && serviceName.isEmpty() == false) {
                File exportFile = nResourceHelper.BuildName("Exported", serviceName + UUID.randomUUID().toString());
                nService currentService = nServiceHelper.GetService(serviceName);

                System.out.println("Exporting statistics to: " + exportFile.getAbsolutePath());
                exporter.Export(currentService, exportFile);
                System.out.println("Exported");
            } else {
                System.out.println("Select a service first");
            }
        }
    }
}
