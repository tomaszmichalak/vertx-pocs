package pl.tomaszmichalak.example.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import pl.tomaszmichalak.example.web.api.ConfigurableHandler;


public class ConfigurableServer extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableServer.class);

  private static final String HANDLERS = "handlers";

  @Override
  public void start() {
    JsonObject config = initConfig();
    Router router = Router.router(vertx);

    List<Handler<RoutingContext>> registeredHandlers = new ArrayList<>();
    ServiceLoader.load(ConfigurableHandler.class).iterator().forEachRemaining(item -> {
          registeredHandlers.add(item.init(router, vertx, config));
          // FIXME incorrect usage of parameters in message
          LOG.info("Registered handler:" + item.getClass().getCanonicalName());
        }
    );

    router.route().handler(BodyHandler.create());
    config.getJsonArray(HANDLERS).forEach(handlerClassName -> {
      registeredHandlers.forEach(handler -> {
        if (handler.getClass().getCanonicalName().equals(handlerClassName)) {
          router.route().handler(handler);
        }
      });
    });
    router.get("/hello").handler(this::handleHello);

    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    LOG.info("Server started on port 8080");
  }

  private JsonObject initConfig() {
    JsonObject config = new JsonObject();
    config.put(HANDLERS,
        new JsonArray().add("pl.tomaszmichalak.example.web.common.CommonHeaderHandler")
            .add("pl.tomaszmichalak.example.web.custom.CustomHeaderHandler"));
    config
        .put("commonHeaderHandler", new JsonObject().put("commonHeaderValue", "CommonHeaderValue"));
    config
        .put("customHeaderHandler", new JsonObject().put("customHeaderValue", "CustomHeaderValue"));
    return config;
  }

  private void handleHello(RoutingContext routingContext) {
    JsonObject arr = new JsonObject();
    arr.put("messaage", "Hello from Vert.x endpoint!");
    routingContext.response().putHeader("content-type", "application/json")
        .end(arr.encodePrettily());
  }


}
