package resources;

import geeks.Geek;
import geeks.Geeks;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Collection;

@Path("/locate")
public class LocateResource extends AbstractResource {
    private final Geeks geeks;

    @Inject
    public LocateResource(Geeks geeks) {
        this.geeks = geeks;
    }

    @GET
    @Produces("application/json;encoding=utf-8")
    public Collection<Geek> locateGeeks(@QueryParam("city") String city) {
        return geeks.locate(city);
    }
}
