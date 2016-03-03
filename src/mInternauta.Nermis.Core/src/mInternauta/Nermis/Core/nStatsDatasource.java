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
package mInternauta.Nermis.Core;

/**
 * It represents a Data Source for RRD
 */
public class nStatsDatasource {
    /**
     * RRD Internal Name
     */
    public String InternalName;
    
    
    /**
     * RRD Name
     */
    public String Name;
        
    /**
     * RRD Type
     */
    public nStatsDataType Type;
    
    /**
     * Check: http://oss.oetiker.ch/rrdtool/doc/rrdcreate.en.html#___top
     */
    public long Heartbeat;
    
    /**
     * Min value for the data
     */
    public double MinValue;
    
    /**
     * Max value for the data
     */
    public double MaxValue;
}
