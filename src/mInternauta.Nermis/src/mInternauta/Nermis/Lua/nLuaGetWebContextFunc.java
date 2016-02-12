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

import mInternauta.Nermis.Web.nWebContext;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * Implements a Function to get the current Web Context for a Lua Script
 * @author marcelo
 */
public class nLuaGetWebContextFunc extends ZeroArgFunction {
    private final nWebContext myContext;

    public nLuaGetWebContextFunc(nWebContext context) {
        myContext =context;
    }

    @Override
    public LuaValue call() {
        return CoerceJavaToLua.coerce(myContext);
    }
}
