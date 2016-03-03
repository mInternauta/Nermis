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
package mInternauta.Nermis.Persistence;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

/**
 * Container Serializer Manages the storage of a container in a resource on the
 * disc.
 */
public class nContainerSerializer {

    private nSerializerOptions _Options;

    public nSerializerOptions getOptions() {
        return _Options;
    }

    public void setOptions(nSerializerOptions _Options) {
        this._Options = _Options;
    }

    public nContainerSerializer() {
        this._Options = new nSerializerOptions();
        this._Options.BinaryMode = false;
    }

    /**
     * Save the container into the resource
     *
     * @param <TContainer> Container Type
     * @param container Container to save
     * @param stream Resource to write
     * @throws Exception
     */
    public <TContainer extends nBaseContainer> void Save(TContainer container, OutputStream stream) throws Exception {
        if(this._Options.BinaryMode == false) {
            XMLEncoder e = new XMLEncoder(stream);
            e.writeObject(container);
            e.close();
        }
        else
        {
            JBossObjectOutputStream e = new JBossObjectOutputStream(stream);
            e.writeObject(container);
            e.close();
        }
    }

    /**
     * Load the container from a resource
     *
     * @param <TContainer> Container Type
     * @param stream Resource to read
     * @return
     * @throws Exception
     */
    public <TContainer extends nBaseContainer> TContainer Load(InputStream stream) throws Exception {
        TContainer container = null;

        if (this._Options.BinaryMode == false) {
            XMLDecoder d = new XMLDecoder(stream);
            container = (TContainer) d.readObject();
            d.close();
        }
        else 
        {
            JBossObjectInputStream d = new JBossObjectInputStream(stream);
            container = (TContainer)d.readObject();
            d.close();
        }

        return container;
    }
}
