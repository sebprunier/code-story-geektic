package geeks;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jongo.MongoCollection;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Singleton
public class Geeks {
    private static final String COLLECTION_NAME = "geeks";

    private MongoCollection geeks;

    @Inject
    public Geeks(@GeeksCollection MongoCollection geeks) {
        this.geeks = geeks;
    }

    public void addGeek(Geek geek) {
        geeks.insert(geek);
    }

    public Collection<Geek> search(String keywords) {
        Set<Geek> friends = Sets.newHashSet();

        if (Strings.isNullOrEmpty(keywords)) {
            return friends;
        }

        List<String> keywordList = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(keywords));
        return Sets.newHashSet(geeks.find("{ 'likes' : {$in : #} }", keywordList).as(Geek.class));
    }

    protected void removeAll() {
        geeks.remove();
    }
}
