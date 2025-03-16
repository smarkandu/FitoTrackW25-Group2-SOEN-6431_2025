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

import java.net.MalformedURLException;
import java.net.URL;

public class MapnikTileSource extends FitoTrackTileSource {

    public static final MapnikTileSource INSTANCE = new MapnikTileSource(new String[]{
            "a.tile.openstreetmap.org", "b.tile.openstreetmap.org", "c.tile.openstreetmap.org"}, 443);

    private static final String NAME = "OSM Mapnik";

    private MapnikTileSource(String[] hostNames, int port) {
        super(hostNames, port);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getParallelRequestsLimit() {
        return PARALLEL_REQUESTS_LIMIT;
    }

    @Override
    public String getTilePath(Tile tile) {
        return "/" + tile.zoomLevel + '/' + tile.tileX + '/' + tile.tileY + ".png";
    }
}
