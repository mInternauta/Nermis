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
package mInternauta.Nermis.Lua;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import mInternauta.Nermis.Web.nWebContext;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

/**
 * Implements the Create Instance for Lua
 */
public class nLuaGetFileFunc extends TwoArgFunction {
    
    @Override
    public LuaValue call(LuaValue context, LuaValue fileNameLV) {
        nWebContext webContext = (nWebContext) CoerceLuaToJava.coerce(context, nWebContext.class);        
        String rootPath = new File(webContext.Target).getParent();
        
        String fileName = fileNameLV.tojstring();
        LuaValue finalValue = null;
        
        Path path = Paths.get(rootPath, fileName);
        finalValue = CoerceJavaToLua.coerce(path.toFile());
        
        return finalValue;
    }

}
