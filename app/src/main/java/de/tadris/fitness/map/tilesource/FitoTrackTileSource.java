/*
 * Copyright (c) 2020 Jannis Scheibe <jannis@tadris.de>
 *
 * This file is part of FitoTrack
 *
 * FitoTrack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FitoTrack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tadris.fitness.map.tilesource;

import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;

public abstract class FitoTrackTileSource extends AbstractTileSource {

    protected byte zoomLevelMax;
    protected byte zoomLevelMin;

    FitoTrackTileSource(String[] hostNames, int port, byte zoomLevelMin, byte zoomLevelMax) {
        super(hostNames, port);
        defaultTimeToLive = 8279000;
        this.zoomLevelMin = zoomLevelMin;
        this.zoomLevelMax = zoomLevelMax;
    }

    @Override
    public boolean hasAlpha() {
        return false;
    }

    public abstract String getName();

    @Override
    public byte getZoomLevelMax() {
        return zoomLevelMax;
    }

    @Override
    public byte getZoomLevelMin() {
        return zoomLevelMin;
    }

}
