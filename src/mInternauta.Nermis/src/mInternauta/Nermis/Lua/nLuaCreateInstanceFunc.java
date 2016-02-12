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

import java.util.logging.Level;
import java.util.logging.Logger;
import mInternauta.Nermis.nController;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * Implements the Create Instance for Lua
 */
public class nLuaCreateInstanceFunc extends OneArgFunction {

    @Override
    public LuaValue call(LuaValue lv) {
        String typeName = lv.tojstring();
        LuaValue finalValue = null;
        
        try
        {
            Class clazz = Class.forName(typeName);
            if(clazz != null) {
                Object instance = clazz.newInstance();
                finalValue = CoerceJavaToLua.coerce(instance);
            }
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            nController.CurrentLogger.log(Level.WARNING, "Error in the Lua.createInstance: " + ex.getMessage());
        }
        
        return finalValue;
    }

}
