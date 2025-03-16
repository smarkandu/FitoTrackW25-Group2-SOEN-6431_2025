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

import org.mapsforge.core.model.Tile;
import org.mapsforge.map.layer.download.tilesource.AbstractTileSource;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class FitoTrackTileSource extends AbstractTileSource {

    protected byte zoomLevelMax;
    protected byte zoomLevelMin;

    protected int PARALLEL_REQUESTS_LIMIT;

    FitoTrackTileSource(String[] hostNames, int port, byte zoomLevelMin, byte zoomLevelMax, int parallelRequestCount) {
        super(hostNames, port);
        defaultTimeToLive = 8279000;
        this.zoomLevelMin = zoomLevelMin;
        this.zoomLevelMax = zoomLevelMax;
        this.PARALLEL_REQUESTS_LIMIT = parallelRequestCount ;
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

    @Override
    public int getParallelRequestsLimit() {
        return PARALLEL_REQUESTS_LIMIT;
    }

    // Template method for constructing the tile URL.
    // This method uses the protocol, host name, and port to build the complete URL.
    @Override
    public URL getTileUrl(Tile tile) throws MalformedURLException {
        return new URL(getProtocol(), getHostName(), this.port, buildTileUrlPath(tile));
    }

    /**
     * Returns the protocol for tile requests.
     * Default implementation returns "https".
     */
    protected String getProtocol() {
        return "https";
    }

    /**
     * Constructs and returns the URL path for the tile.
     * Subclasses must implement this method to provide their specific URL path,
     * which may include extra segments or query parameters.
     */
    protected abstract String buildTileUrlPath(Tile tile);
}
