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
package mInternauta.Nermis.Builtin.Watchers;

import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceResults;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import mInternauta.Nermis.Core.nServiceState;
import mInternauta.Nermis.Core.nServiceWatcher;

/**
 * Analyzes a database with ODBC support is operational.
 * This watcher uses JDBC to connect to the database so its important that the JDBC driver is installed.
 * <p>
 * Extended Properties:
 * Driver => JDBC Driver Name (com.****.jdbc.Driver)
 * Provider => The database provider name (mysql,sqlserver,postgre)
 * Database => The database name
 * Username => The Database username
 * Password => The database password
 * Hostname => The Server hostname and port (mysqlconnection.com:3399)
 */
public class nOdbcWatcher implements nServiceWatcher {

    @Override
    public String getName() {
        return "Odbc";
    }

    @Override
    public nServiceResults execute(nService service) {
        nServiceResults results = new nServiceResults();
        
        try {
            // Try to load the JDBC Driver
            Class.forName(service.Properties.get("Driver"));
            
            // Try to create the connection
            String url = "jdbc:" + service.Properties.get("Provider") + "://"
                    + service.Properties.get("Hostname") + "/" + service.Properties.get("Database");
            
            Connection conn = DriverManager.getConnection(url, 
                    service.Properties.get("Username"), service.Properties.get("Password"));
            
            // Check the connection state
            if(conn.isValid(10)) {
                results.State = nServiceState.ONLINE;
            }
            
            conn.close();
        } catch (ClassNotFoundException ex) {
            results.Message = ex.getLocalizedMessage();
            results.State = nServiceState.CANT_EXECUTE;
        } catch (SQLException ex) {
            results.Message = ex.getLocalizedMessage();
            results.State = nServiceState.OFFLINE;
        }
        
        return results;
    }

    @Override
    public boolean validate(nService service) {
        boolean isValid = false;
        
        if(service != null && service.Properties != null) {
            isValid = service.Properties.containsKey("Driver") && 
                    service.Properties.containsKey("Provider") && 
                    service.Properties.containsKey("Database") &&
                    service.Properties.containsKey("Hostname") &&
                    service.Properties.containsKey("Username") &&
                     service.Properties.containsKey("Password");
        }
        
        return isValid;
    }

    @Override
    public HashMap<String, String> getExtPropertiesHelp() {
        HashMap<String, String> props = new HashMap<>();
        
        props.put("Driver", "JDBC Driver Name (com.****.jdbc.Driver)");
        props.put("Provider", "The database provider name (mysql,sqlserver,postgre)");
        props.put("Database", "The database name");
        props.put("Username", "The Database username");
        props.put("Password", "The Database password");
        props.put("Hostname", "The Server hostname and port (mysqlconnection.com:3399)");
  
        return props;
    }
    
}
