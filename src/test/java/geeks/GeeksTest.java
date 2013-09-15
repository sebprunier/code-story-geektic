package geeks;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GeeksTest {

    private Geeks geeks;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new GeeksModule());
        geeks = injector.getInstance(Geeks.class);

        // clear embedded db
        geeks.removeAll();
    }

    @Test
    public void no_geek_matches_empty_keywords() {
        geeks.addGeek(geek("Xavier", "image", "java"));
        geeks.addGeek(geek("Martin", "image", "scala"));

        assertThat(geeks.search("")).isEmpty();
    }

    @Test
    public void should_search_java_geek() {
        geeks.addGeek(geek("Xavier", "http://exemple.org/sample.jpg", "java"));

        Collection<Geek> javaGeeks = geeks.search("java");
        assertThat(javaGeeks).onProperty("nom").containsOnly("Xavier");
        assertThat(javaGeeks).onProperty("imageUrl").containsOnly("http://exemple.org/sample.jpg");
    }

    @Test
    public void should_search_one_java_geek() {
        geeks.addGeek(geek("Xavier", "image", "java"));
        geeks.addGeek(geek("Martin", "image", "scala"));

        assertThat(geeks.search("scala")).onProperty("nom").containsOnly("Martin");
    }

    @Test
    public void should_search_case_insensitive() {
        geeks.addGeek(geek("Xavier", "image", "java"));
        geeks.addGeek(geek("Martin", "image", "scala"));

        assertThat(geeks.search("SCaLa")).onProperty("nom").containsOnly("Martin");
    }

    @Test
    public void should_search_on_any_keyword() {
        geeks.addGeek(geek("Xavier", "image", "java", "coffee"));

        assertThat(geeks.search("coffee")).onProperty("nom").containsOnly("Xavier");
    }

    @Test
    public void should_search_on_two_keywords() {
        geeks.addGeek(geek("Xavier", "image", "java", "coffee"));
        geeks.addGeek(geek("Christophe", "image", "java", "linux"));

        assertThat(geeks.search("coffee linux")).onProperty("nom").containsOnly("Xavier", "Christophe");
    }

    @Test
    public void should_search_on_two_keywords_avoid_duplaicates() {
        geeks.addGeek(geek("Xavier", "image", "java", "coffee"));
        geeks.addGeek(geek("Christophe", "image", "java", "linux"));

        assertThat(geeks.search("coffee java")).hasSize(2);
    }

    @Test
    public void should_store_geeks() throws Exception {
        geeks.addGeek(geek("Azerty", "image", "rien"));
        assertThat(geeks.search("rien")).onProperty("nom").containsOnly("Azerty");
    }

    @Test
    public void should_remove_geeks() throws Exception {
        geeks.addGeek(geek("Furtif", "image", "deletion"));
        assertThat(geeks.search("deletion")).hasSize(1);
        geeks.removeAll();
        assertThat(geeks.search("deletion")).hasSize(0);
    }

    static Geek geek(String name, String imageUrl, String... likes) {
        Geek geek = new Geek();
        geek.setNom(name);
        geek.setLikes(likes);
        geek.setImageUrl(imageUrl);
        return geek;
    }
}
