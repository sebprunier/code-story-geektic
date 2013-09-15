package geeks;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class TextSearchResult {

    List<GeekWithScore> results;

    public List<GeekWithScore> getResults() {
        return results;
    }

    public void setResults(List<GeekWithScore> results) {
        this.results = results;
    }

    public static final class GeekWithScore {

        private Double score;

        private Geek obj;

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public Geek getObj() {
            return obj;
        }

        public void setObj(Geek obj) {
            this.obj = obj;
        }
    }

}
