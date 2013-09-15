package geeks;

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

    protected void removeAll() {
        geeks.remove();
    }
}
