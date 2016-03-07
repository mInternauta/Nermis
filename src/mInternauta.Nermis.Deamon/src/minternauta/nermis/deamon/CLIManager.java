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
package minternauta.nermis.deamon;

import java.util.ArrayList;
import java.util.Scanner;
import minternauta.nermis.deamon.CLI.CLIExecute;
import minternauta.nermis.deamon.CLI.CLIService;
import minternauta.nermis.deamon.CLI.CLISettings;
import minternauta.nermis.deamon.CLI.CLIStatistics;
import minternauta.nermis.deamon.CLI.ICLICommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLIManager {
    private DefaultParser parser;
    private ArrayList<ICLICommand> commands;
    
    public void Setup() {        
        // - Setup all Commands
        this.commands = new ArrayList<>();
        this.commands.add(new CLIExecute());
        this.commands.add(new CLIService());
        this.commands.add(new CLISettings());
        this.commands.add(new CLIStatistics());
        
        // -
        this.parser = new DefaultParser();
    }
    
    public void Run() {
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        
        while(true) {
            System.out.print("#> ");
            String data = scanner.nextLine();
            
            if(data.isEmpty() == false) {
                try {                    
                    // - Parse the Command
                    String[] args = data.split(" ");
                    String firstArg = args[0];
                    
                    parseCommands(firstArg, args);
                } catch (ParseException ex) {
                    System.out.println(ex);
                }
            }
            System.out.println();
        }
    }

    private void parseCommands(String firstArg, String[] args) throws ParseException {      
        if(firstArg.equalsIgnoreCase("exit")) {
            Main.Controller.Stop();
            System.exit(0);
        }
        else if(firstArg.equalsIgnoreCase("help")) {
            System.out.println("Current help commands: ");
            for(ICLICommand cmd : this.commands) {
                System.out.println(cmd.getName() + " -help");
            }
        }
        else if(args.length > 0)
        {
            String cmdName = args[0].trim();
            for(ICLICommand cmd : this.commands) {
                if(cmd.getName().equalsIgnoreCase(cmdName)) {
                    Options opts = cmd.getOptions();
                    CommandLine cmdLine = parser.parse(opts, args);
                    
                    if(cmdLine.hasOption("help")) {
                        HelpFormatter formatter = new HelpFormatter();
                        formatter.printHelp( cmdName, opts );
                    } else {
                        cmd.Execute(cmdLine);
                    }
                    
                    break;
                }
            }
        }
    }
}
