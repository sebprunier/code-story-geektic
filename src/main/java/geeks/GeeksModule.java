package geeks;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

public class GeeksModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    Jongo provideJongo() throws Exception {
        return new Jongo(new MongoClient().getDB("geeksDB"));
    }
}
