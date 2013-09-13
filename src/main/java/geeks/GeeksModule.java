package geeks;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import geeks.GeeksCollection;
import org.jongo.Jongo;

public class GeeksModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @GeeksCollection
    @Singleton
    org.jongo.MongoCollection provideGeeksCollection() throws Exception {
        return new Jongo(new MongoClient().getDB("geeksDB")).getCollection("geeks");
    }
}
