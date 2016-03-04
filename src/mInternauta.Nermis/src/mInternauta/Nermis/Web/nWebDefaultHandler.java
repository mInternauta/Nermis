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
package mInternauta.Nermis.Web;

import mInternauta.Nermis.Lua.nLuaGetFileFunc;
import mInternauta.Nermis.Lua.nLuaGetWebContextFunc;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;

/**
 * Default Web Handler for the Jetty
 */
public class nWebDefaultHandler extends AbstractHandler {
    private HashMap<String, AbstractHandler> handlerMaps = new HashMap<>();   
    private final String webServerPath;
    private String[] welcomeFiles;
    
    nWebDefaultHandler(String webServerPath) {
        this.webServerPath = webServerPath;
    }
    
    /**
     * Clear all mappings 
     */
    public void clearMapping() {
        handlerMaps.clear();
    }
    
    /**
     * Add a Handler Mapping to a Context Path
     * @param contextPath
     * @param handler 
     */
    public void addMapping(String contextPath, AbstractHandler handler) {
        handlerMaps.put(contextPath, handler);
    }
    
    @Override
    public void handle(String target, Request req, HttpServletRequest hreq, HttpServletResponse resp) throws IOException, ServletException {
        try 
        {
            target = target.toLowerCase();
            String targetPath = "";
            
            if(target.endsWith("/")) {
                targetPath = searchWelcomeFile(target);
            } 
            else 
            {            
                targetPath = Paths.get(webServerPath, target).toString();
            }
            
            if(targetPath.endsWith(".lua")) {
                processLuaScript(targetPath, hreq, resp);
            }
            else if(handlerMaps.containsKey(target.trim()))
            {
                handlerMaps.get(target.trim()).handle(target, req, hreq, resp);
            }
            else 
            {
                sendResource(targetPath, resp);
            }
        }
        catch(IOException | ServletException exp)
        {
            CurrentLogger.log(Level.SEVERE, "Web Server Error: ");
            CurrentLogger.log(Level.SEVERE, exp.toString());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String searchWelcomeFile(String targetPath) {
        for(String wlcFile : this.welcomeFiles) {
            File file = Paths.get(webServerPath, targetPath, wlcFile).toFile();
            if(file.exists()) {
                targetPath = file.getAbsolutePath();
                break;
            }
        }
        return targetPath;
    }

    private void sendResource(String targetPath, HttpServletResponse resp) throws IOException {
        File file = new File(targetPath);
        if(file.exists()) {
            String contentType = Files.probeContentType(file.toPath());
            
            OutputStream output = resp.getOutputStream();
            InputStream input = new FileInputStream(file);
            
            IOUtils.copy(input, output);
            input.close();
            
            resp.setStatus(200);
            resp.setContentType(contentType);            
            
            resp.flushBuffer();
        } else {
            resp.setStatus(404);
        }
    }
 
    private void processLuaScript(String target, HttpServletRequest req, HttpServletResponse resp) throws IOException {
          String path = target;
          File file = new File(path);

          if(file.exists()) {
              nWebContext Context = new nWebContext();
              Context.Target = path;
              Context.Request = req;
              Context.Response = resp;

              // Create the global runtime
              Globals globals = JsePlatform.standardGlobals();
              globals.set("getWebContext", new nLuaGetWebContextFunc(Context));
              globals.set("getFile", new nLuaGetFileFunc());

              // Load the file
              LuaValue script = globals.loadfile(path);
              script.call();
              
              // Flush the data
              resp.flushBuffer();
          }
          else 
          {
              resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          }
      }

    public void setWelcomeFiles(String[] list) {
        this.welcomeFiles = list;
    }

}
