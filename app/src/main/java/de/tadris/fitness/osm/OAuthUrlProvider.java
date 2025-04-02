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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

class OAuthUrlProvider {
    private static final Properties properties = new Properties();

    // Static initializer to load properties
    static {
        try (InputStream input = OAuthUrlProvider.class.getClassLoader().getResourceAsStream("oauth.properties")) {
            if (input == null) {
                throw new IllegalStateException("oauth.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading OAuth properties", e);
        }
    }

    // Retrieve credentials from properties
    private static final String CONSUMER_KEY = properties.getProperty("osm.oauth.consumer.key");
    private static final String CONSUMER_SECRET = properties.getProperty("osm.oauth.consumer.secret");

    // OAuth URLs remain the same
    private static final String URL_TOKEN_REQUEST = "https://www.openstreetmap.org/oauth/request_token";
    private static final String URL_TOKEN_ACCESS = "https://www.openstreetmap.org/oauth/access_token";
    private static final String URL_AUTHORIZE = "https://www.openstreetmap.org/oauth/authorize";

    // Method to get OAuth Consumer
    public static OAuthConsumer getDefaultConsumer() {
        if (CONSUMER_KEY == null || CONSUMER_SECRET == null) {
            throw new IllegalStateException("OAuth credentials are not properly configured");
        }
        return new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    }

    // Method to get OAuth Provider
    public static OAuthProvider getDefaultProvider() {
        return new DefaultOAuthProvider(URL_TOKEN_REQUEST, URL_TOKEN_ACCESS, URL_AUTHORIZE);
    }
}
