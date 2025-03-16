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

    protected static final String PROTOCOL = "https";
    protected static final int PARALLEL_REQUESTS_LIMIT = 8;

    FitoTrackTileSource(String[] hostNames, int port) {
        super(hostNames, port);
        defaultTimeToLive = 8279000;
    }

    @Override
    public boolean hasAlpha() {
        return false;
    }

    public abstract String getName();


    public abstract String getTilePath(Tile tile);

    public URL getTileUrl(Tile tile) throws MalformedURLException {
        return new URL(PROTOCOL, getHostName(), this.port, getTilePath(tile));
    }

    public byte getZoomLevelMax() {
        return 18;  // Default value, can be overridden
    }

    public byte getZoomLevelMin() {
        return 0;   // Default value
    }
}
