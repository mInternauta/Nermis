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
package mInternauta.Nermis.Notifiers;

import java.util.logging.Level;
import mInternauta.Nermis.Core.nService;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;

/**
 * Simple Console Notifier
 */
public class nConsoleNotifier extends nAbstractNotifier {

    @Override
    public void NotifyServiceOffine(nService service, String message) {
        CurrentLogger.log(Level.INFO, "Service down: " + message);
    }

    @Override
    public void NotifyServerError(String message) {
        CurrentLogger.log(Level.INFO, "Server Error: " + message);
    }

    @Override
    public String[] getProperties() {
        return new String[] {""};
    }

    @Override
    public String getName() {
        return "Console";
    }
    
}
