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

package de.tadris.fitness.osm;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class OAuthUrlProvider {

    private static final String CONSUMER_KEY = loadConfig("OSM_CONSUMER_KEY");
    private static final String CONSUMER_SECRET = loadConfig("OSM_CONSUMER_SECRET");

    private static String loadConfig(String key) {
        Properties properties = new Properties();
        try (InputStream input = OAuthUrlProvider.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing config.properties file. Please add it under app/src/main/resources.");
            }
            properties.load(input);
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config file", e);
        }
    }

    static OAuthConsumer getDefaultConsumer() {
        return new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    }

    static OAuthProvider getDefaultProvider() {
        return new DefaultOAuthProvider(URL_TOKEN_REQUEST, URL_TOKEN_ACCESS, URL_AUTHORIZE);
    }

    static private final String URL_TOKEN_REQUEST = "https://www.openstreetmap.org/oauth/request_token";
    static private final String URL_TOKEN_ACCESS = "https://www.openstreetmap.org/oauth/access_token";
    static private final String URL_AUTHORIZE = "https://www.openstreetmap.org/oauth/authorize";
}

