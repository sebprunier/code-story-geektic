package geeks;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.Collection;
import java.util.Set;

@Singleton
public class Geeks {
    private static final String COLLECTION_NAME = "geeks";
    private static final Integer MAX_DISTANCE = 10000;

    private Jongo jongo;
    private MongoCollection geeks;

    @Inject
    public Geeks(Jongo jongo) {
        this.jongo = jongo;
        this.geeks = jongo.getCollection(COLLECTION_NAME);
    }

    public void addGeek(Geek geek) {
        geeks.insert(geek);
    }

    public Collection<Geek> search(String keywords) {
        Set<Geek> friends = Sets.newHashSet();

        if (Strings.isNullOrEmpty(keywords)) {
            return friends;
        }

        // Text search hack with mongo-java : http://stackoverflow.com/questions/16977295/how-to-execute-mongo-query-db-collection-runcommandtext-searchsearch-tex
        TextSearchResult result = jongo.runCommand("{text : #, search : #}", COLLECTION_NAME, keywords).as(TextSearchResult.class);
        return Lists.transform(result.getResults(), new Function<TextSearchResult.GeekWithScore, Geek>() {
            @Override
            public Geek apply(TextSearchResult.GeekWithScore geekWithScore) {
                return geekWithScore.getObj();
            }
        });
    }

    public Collection<Geek> locate(String city) {
        Set<Geek> friends = Sets.newHashSet();

        if (Strings.isNullOrEmpty(city)) {
            return friends;
        }

        // Search city coordinates
        Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(city).getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
        if (!"OK".equals(geocoderResponse.getStatus().value())) {
            return friends;
        }
        LatLng cityLocation = geocoderResponse.getResults().get(0).getGeometry().getLocation();
        Double longitude = cityLocation.getLng().doubleValue();
        Double latitude = cityLocation.getLat().doubleValue();

        // GeoNear query
        Iterable<Geek> geeksIt = geeks.find("{location : {$near : {$geometry : {type : \"Point\" , coordinates : [#, #]}, $maxDistance : #}}}", longitude, latitude, MAX_DISTANCE).as(Geek.class);
        return Sets.newHashSet(geeksIt);
    }

    protected void removeAll() {
        geeks.remove();
    }
}
