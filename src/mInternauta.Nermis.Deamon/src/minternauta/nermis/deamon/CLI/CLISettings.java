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
package minternauta.nermis.deamon.CLI;

import java.io.File;
import java.util.HashMap;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Configs.nConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CLISettings implements ICLICommand {

    // TODO: Implements the commands to configure the Notifier
    
    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public Options getOptions() {
        Options settings = new Options();
        settings.addOption(new Option("help", "Display command help"));
        
        settings.addOption(
                Option.builder("set")
                .hasArgs()
                .valueSeparator()
                .desc("Set a setting, see -view for setting list")
                .build());
        
        settings.addOption(
                Option.builder("installjar")
                .hasArgs()
                .numberOfArgs(2)
                .valueSeparator()
                .desc("Install a jar file to the Nermis")
                .build());
        
        settings.addOption(
                Option.builder("view")
                .desc("Show the current settings")
                .build());
        
        return settings;
    }

    @Override
    public void Execute(CommandLine cmd) {
        viewCommand(cmd);        
        setCommand(cmd);  
        installjarCommand(cmd);
    }

    private void setCommand(CommandLine cmd) throws NumberFormatException {
        if(cmd.hasOption("set")) {
            String propertyName = cmd.getOptionValues("set")[0];
            String propertyValue = cmd.getOptionValues("set")[1];
            propertyValue = propertyValue.replace("\\0", " ");                
            nConfiguration cfg = nConfigHelper.getConfiguration();
            
            if(propertyName.equalsIgnoreCase("WebServerPort")) {
                cfg.WebServerPort = Integer.valueOf(propertyValue);
            }
            
            if(propertyName.equalsIgnoreCase("Language")) {
                cfg.Language = propertyValue;
            }
            
            nConfigHelper.Save(cfg);
        }
    }
    
     private void installjarCommand(CommandLine cmd) throws NumberFormatException {
        if(cmd.hasOption("installjar")) {
            String name = cmd.getOptionValues("installjar")[0];
            String jar = cmd.getOptionValues("installjar")[1];
            jar = jar.replace("\\0", " ");                
            nConfiguration cfg = nConfigHelper.getConfiguration();
            
            File file = new File(jar);
            
            if(file.exists()) {
                if(cfg.IncludedJars == null) {
                    cfg.IncludedJars = new HashMap<>();
                }
                cfg.IncludedJars.put(name.trim(), jar);
                System.out.println("Jar included to the Nermis, restart to take effect.");
            } else {
                System.out.println("Cant find the jar file: " + file.getAbsolutePath());
            }
            
            nConfigHelper.Save(cfg);
        }
    }

    private void viewCommand(CommandLine cmd) {
        if(cmd.hasOption("view")) {
            System.out.println("Current settings: ");
            System.out.println("WebServerPort: " + nConfigHelper.getConfiguration().WebServerPort);
            System.out.println("Language: " + nConfigHelper.getConfiguration().Language);
        }
    }
    
}
