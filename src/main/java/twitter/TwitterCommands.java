package twitter;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;
import com.google.inject.Inject;
import geeks.Geek;
import geeks.Geeks;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterables.toArray;

public class TwitterCommands {
    public static final Pattern TWEET_PATTERN = Pattern.compile(".*#geektic (.+)");

    private final Geeks geeks;

    @Inject
    public TwitterCommands(Geeks geeks) {
        this.geeks = geeks;
    }

    public void onTweet(Status status) {
        String text = status.getText();

        Matcher matcher = TWEET_PATTERN.matcher(text);
        if (!matcher.matches()) {
            return;
        }

        String[] likes = toArray(on(' ').split(matcher.group(1)), String.class);

        Geek geek = new Geek();
        geek.setNom(status.getUser().getName());
        geek.setLikes(likes);

        String profileImageURL = status.getUser().getBiggerProfileImageURL();
        if (profileImageURL != null) {
            geek.setImageUrl(profileImageURL);
        }

        String city = status.getUser().getLocation();
        if (city != null) {
            geek.setVille(city);
            geek.setLocation(geekLocationFromCityName(geek.getVille()));
        } else {
            // try to retrieve GeoLocation
            GeoLocation geoLoc = status.getGeoLocation();
            if (geoLoc != null) {
                double longitude = geoLoc.getLongitude();
                double latitude = geoLoc.getLatitude();
                Geek.Location location = newGeekLocation(longitude, latitude);
                geek.setLocation(location);
            } else {
                // try to retrieve Place
                Place place = status.getPlace();
                if (place != null) {
                    geek.setVille(place.getFullName());
                    geek.setLocation(geekLocationFromCityName(geek.getVille()));
                }
            }
        }

        geeks.addGeek(geek);
    }

    private Geek.Location geekLocationFromCityName(String ville) {
        final Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(ville).getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        if ("OK".equals(geocoderResponse.getStatus().value())) {
            LatLng cityLocation = geocoderResponse.getResults().get(0).getGeometry().getLocation();
            Double longitude = cityLocation.getLng().doubleValue();
            Double latitude = cityLocation.getLat().doubleValue();
            return newGeekLocation(longitude, latitude);
        }
        return null;
    }

    private Geek.Location newGeekLocation(double longitude, double latitude) {
        Geek.Location location = new Geek.Location();
        location.setType("Point");
        location.setCoordinates(new Double[]{longitude, latitude});
        return location;
    }
}
