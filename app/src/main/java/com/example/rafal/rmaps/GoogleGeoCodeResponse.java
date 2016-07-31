package com.example.rafal.rmaps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by rafal on 29.07.16.
 */
public class GoogleGeoCodeResponse {
    public String status;
    public results[] results;

    public GoogleGeoCodeResponse() {
    }

    public class results {
        public String formatted_address;
        public geometry geometry;
        public String[] types;
        public address_component[] address_components;
    }

    public class geometry {
        public bounds bounds;
        public String location_type;
        public location location;
        public bounds viewport;
    }

    public class bounds {

        public location northeast;
        public location southwest;

        LatLngBounds getBounds() {
            return new LatLngBounds(southwest.getLatLng(), northeast.getLatLng());
        }
    }

    public class location {
        public double lat;
        public double lng;

        LatLng getLatLng() {
            return new LatLng(lat, lng);
        }
    }

    public class address_component {
        public String long_name;
        public String short_name;
        public String[] types;
    }
}
