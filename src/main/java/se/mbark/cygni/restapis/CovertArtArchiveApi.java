package se.mbark.cygni.restapis;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * Created by mbark on 04/04/16.
 */
public class CovertArtArchiveApi {
    private static final String URL = "http://coverartarchive.org/";
    private static final String RELEASE_GROUP = "release-group/";
    private static final String FRONT = "/front";
    final private static Logger LOGGER = LoggerFactory.getLogger("se.mbark.cygni.util.CoverArtArchiveApi");

    private final HttpClient client;

    public CovertArtArchiveApi(Vertx vertx) {
        client = vertx.createHttpClient();
    }

    public void getAlbumCover(String mbid, Consumer<JsonObject> success, BiConsumer<Integer, String> fail) {
        String url = buildUrl(mbid);

        LOGGER.debug("GET CoverArt image from {0}", url);
        client.getAbs(url, response -> {
            if(response.statusCode() == 307 || response.statusCode() == 302) {
                String location = response.getHeader("Location");
                JsonObject image = new JsonObject();
                image.put("image", location);

                LOGGER.debug("GET CoverArt image from {0} got response {1}", url, image);
                success.accept(image);
            } else {
                LOGGER.debug("GET cover art image from {0} failed with status code {1}", url, response.statusCode());
                fail.accept(response.statusCode(), "");
            }
        }).end();
    }

    private String buildUrl(String mbid) {
        String url = URL + RELEASE_GROUP + mbid + FRONT;
        return url;
    }
}
