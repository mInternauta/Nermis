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
package mInternauta.Nermis.StatsAnalyser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import mInternauta.Nermis.Core.nStatisticsData;
import mInternauta.Nermis.Statistics.nStatsController;

/**
 * Analyser Form Logic
 */
public class nAnalyser {
    private ArrayList<nStatisticsData> cData;
    
    public void Load(File file) {
        this.cData = nStatsController.getStatsManager().Load(file);
    }
    
    public void Fill(DefaultTableModel model) {
        SimpleDateFormat format = new SimpleDateFormat();
        
        for(nStatisticsData stat : this.cData) {
            Date time = new Date(stat.Time);
            model.addRow(new Object[] { stat.DataSource, format.format(time), stat.Value});
        }
    }
}
