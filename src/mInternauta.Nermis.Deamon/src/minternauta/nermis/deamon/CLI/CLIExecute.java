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

import java.util.HashMap;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Persistence.nServiceHelper;
import minternauta.nermis.deamon.Main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CLIExecute implements ICLICommand {

    @Override
    public String getName() {
        return "execute";
    }

    @Override
    public Options getOptions() {
        Options executeOptions = new Options();
        executeOptions.addOption(new Option("createexample", "Create a example setting"));
        executeOptions.addOption(new Option("help", "Display command help"));
        executeOptions.addOption(new Option("start", "Starts the controller"));
        executeOptions.addOption(new Option("stop", "Stops the controller"));
        
        return executeOptions;
    }

    @Override
    public void Execute(CommandLine cmd) {
          if(cmd.hasOption("createexample")) {
            createExample();
        }
        
        if(cmd.hasOption("start")) {
            Main.Controller.Start();
        }    
        
        if(cmd.hasOption("stop")) {
            Main.Controller.Stop();
        }
    }
    
    private void createExample() {
        System.out.println("Creating the example setting...");
        
        nService service = new nService();
        service.Name = "MyExample";
        service.Description = "My Example Service";
        service.RefUrl = "http://mywebsite.com/MyExample";
        service.RepeatEvery = 0; 
        service.Watcher = "Web";
        
        service.Properties = new HashMap<String,String>();
        service.Properties.put("Url", "www.google.com.br");
        service.Properties.put("Method", "GET");
        service.Properties.put("Protocol", "http");
        
        // Save
        nServiceHelper.AddService(service);
    }
    
}
