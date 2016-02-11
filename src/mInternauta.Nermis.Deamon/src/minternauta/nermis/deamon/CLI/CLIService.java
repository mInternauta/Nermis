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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Persistence.nServiceHelper;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.nController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CLIService implements ICLICommand {

    private String selectedService;

    @Override
    public String getName() {
        return "service";
    }

    @Override
    public Options getOptions() {
        
        Options servicesOptions = new Options();
        servicesOptions.addOption(new Option("help", "Display command help"));
        servicesOptions.addOption(
                Option.builder("create")
                .hasArg()
                .desc("Create a new service")
                .build());
        servicesOptions.addOption(
                Option.builder("select")
                .hasArg()
                .desc("Select the service")
                .build());
        servicesOptions.addOption(
                Option.builder("remove")
                .desc("Remove the service")
                .build());
        servicesOptions.addOption(
                Option.builder("setprop")
                .hasArgs()
                .valueSeparator()
                .desc("Set a service property, use \\0 for blank space in the value")
                .build());
        servicesOptions.addOption(
                Option.builder("rmprop")
                .hasArg()
                .desc("Remove a service property")
                .build());
        servicesOptions.addOption(
                Option.builder("view")
                .desc("Show the current service")
                .build());
        servicesOptions.addOption(
                Option.builder("list")
                .desc("List all services")
                .build());
        servicesOptions.addOption(
                Option.builder("watchers")
                .desc("List all watchers")
                .build());
        servicesOptions.addOption(
                Option.builder("watcherprops")
                .hasArg()
                .desc("List all watcher properties")
                .build());
        
        return servicesOptions;
    }

    @Override
    public void Execute(CommandLine cmd) {
        // Create Service
        createService(cmd);
        
        // Remove Service
        removeService(cmd);
        
        // Select the Service
        selectService(cmd);
        
        // Set Service Property
        setPropService(cmd);
        
        // Remove a Service Property
        removeProperty(cmd);
        
        // View current service
        viewService(cmd);
        
        // All Services
        allServices(cmd);
        
        allWatchers(cmd);
        
        listWatcherProps(cmd);
    }
    
     private void allWatchers(CommandLine cmd) {
        // List all Services
        if(cmd.hasOption("watchers")) {
            for(nServiceWatcher watcher : nController.getRegistredWatchers()) {
                System.out.printf("\r\nName: %s", watcher.getName());
            }
        }
    }

    private void allServices(CommandLine cmd) {
        // List all Services
        if(cmd.hasOption("list")) {
            ArrayList<nService> services = nServiceHelper.AllServices();
            for(nService service : services) {
                System.out.printf("\r\nName: %s", service.Name);
            }
        }
    }

    private void viewService(CommandLine cmd) {
        if(cmd.hasOption("view")) {
            String serviceName = this.selectedService;
            
            if(serviceName != null && serviceName.isEmpty() == false) {               
                this.selectedService = serviceName;
                nService currentService = nServiceHelper.GetService(serviceName);
                
                if(currentService != null) {
                    System.out.printf("\r\nName: %s", currentService.Name);
                    System.out.printf("\r\nDescription: %s", currentService.Description);
                    System.out.printf("\r\nRef URL: %s", currentService.RefUrl);
                    System.out.printf("\r\nRepeat Every: %s Seconds" , currentService.RepeatEvery);
                    System.out.printf("\r\nWatcher: %s" , currentService.Watcher);
                    
                    System.out.printf("\r\nExtended Properties: ");
                    if(currentService.Properties != null) {
                        for(Entry<String,String> entry : currentService.Properties.entrySet()) 
                        {
                            System.out.printf("\r\n%s:%s" , entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    System.out.println("Cant fetch current service information: " + this.selectedService);
                }
            } else {
                System.out.println("Select a service first");
            }
        }
    }

    private void removeProperty(CommandLine cmd) {
        // Remove service Property
        if(cmd.hasOption("rmprop")) {
            String serviceName = this.selectedService;
            
            if(serviceName != null && serviceName.isEmpty() == false) {               
                String propertyName = cmd.getOptionValue("setprop");
                
                nServiceHelper.RemoveProperty(propertyName, serviceName);
                System.out.println("Property setted");
            } else {
                System.out.println("Select a service first");
            }
        }
    }

    private void setPropService(CommandLine cmd) {
        // Set service Property
        if(cmd.hasOption("setprop")) {
            String serviceName = this.selectedService;
            
            if(serviceName != null && serviceName.isEmpty() == false) {               
                String propertyName = cmd.getOptionValues("setprop")[0];
                String propertyValue = cmd.getOptionValues("setprop")[1];
                
                propertyValue = propertyValue.replace("\\0", " ");
                
                nServiceHelper.SetProperty(propertyName, propertyValue, serviceName);
                System.out.println("Property setted");
            } else {
                System.out.println("Select a service first");
            }
        }
    }

    private void selectService(CommandLine cmd) {
        // Set a propery
        if(cmd.hasOption("select")) {
            String serviceName = cmd.getOptionValue("select");
            
            if(serviceName != null && serviceName.isEmpty() == false) {               
                this.selectedService = serviceName;
                System.out.println("Service selected");
            } else {
                System.out.println("Cant select a service with blank name");
            }
        }
    }
    
    private void listWatcherProps(CommandLine cmd) {
        // List all watcher properties
        if(cmd.hasOption("watcherprops")) {
            String watcherName = cmd.getOptionValue("watcherprops");
            
            if(watcherName != null && watcherName.isEmpty() == false) {               
                for(nServiceWatcher watcher : nController.getRegistredWatchers()) {
                    if(watcher.getName().equalsIgnoreCase(watcherName)) {
                        System.out.println("Watcher Properties: ");
                        for(Entry<String,String> entry : watcher.getExtPropertiesHelp().entrySet()) {
                            System.out.println(entry.getKey() + " = " + entry.getValue());
                        }
                        break;
                    }
                }
            } else {
                System.out.println("Cant list properties from a watcher with blank name");
            }
                
            System.out.println("Extended Web Server Properties: ");
            System.out.println("ShowInWeb = 0 - Dont show the service in the web page page / 1 - show the service");    
            System.out.println("WebOrder = A Number to order by list (Default: 0)");
        }
    }

    private void removeService(CommandLine cmd) {
        // Remove Service
        if(cmd.hasOption("remove")) {
            String serviceName = this.selectedService;
            
            if(serviceName != null && serviceName.isEmpty() == false) {               
                nServiceHelper.RemoveService(serviceName);
                System.out.println("Service removed");
            } else {
                System.out.println("Select a service first");
            }
        }
    }

    private void createService(CommandLine cmd) {
        if(cmd.hasOption("create")) {
            String serviceName = cmd.getOptionValue("create");
            
            if(serviceName != null && serviceName.isEmpty() == false) {
                nService service = new nService();
                service.Name = serviceName;
                service.Properties  = new HashMap<String,String>();
                
                nServiceHelper.AddService(service);
                System.out.println("New service added");
            } else {
                System.out.println("Cant create a service with blank name");
            }
        }
    }
    
}
