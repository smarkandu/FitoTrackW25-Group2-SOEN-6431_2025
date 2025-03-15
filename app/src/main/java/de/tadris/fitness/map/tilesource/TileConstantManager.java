package de.tadris.fitness.map.tilesource;

/**
 * TileConstantManager Implemented as a singleton, such that there's only
 * one instance available, saving memory (including for its constants)
 */
public class TileConstantManager {
    private static TileConstantManager instance = null;
    private final String HUMANITARIAN_NAME = "Humanitarian";
    private final String OSM_MAPNIK_NAME = "OSM Mapnik";
    private final String HTTPS_PROTOCOL = "https";

    private TileConstantManager() {}

    static TileConstantManager getInstance() {
        if (instance == null)
        {
            instance = new TileConstantManager();
        }

        return instance;
    }

    public String getHUMANITARIAN_NAME() {
        return HUMANITARIAN_NAME;
    }

    public String getOSM_MAPNIK_NAME() {
        return OSM_MAPNIK_NAME;
    }

    public String getHTTPS_PROTOCOL() {
        return HTTPS_PROTOCOL;
    }
}
