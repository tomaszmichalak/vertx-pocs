package pl.tomaszmichalak.example.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.commons.io.IOUtils;
import pl.tomaszmichalak.example.web.api.ConfigurableHandler;
import pl.tomaszmichalak.example.web.api.ConfigurableHandlerFactory;


public class ConfigurableServer extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableServer.class);

  private static final String HANDLERS = "handlers";

  @Override
  public void start() {
    JsonObject config = initConfig();
    LOG.info("Application config :" + config);

    Router router = Router.router(vertx);

    List<ConfigurableHandlerFactory> registeredFactories = new ArrayList<>();
    ServiceLoader.load(ConfigurableHandlerFactory.class).iterator().forEachRemaining(item -> {
          registeredFactories.add(item);
          LOG.info("Registered handler:" + item.getClass().getCanonicalName());
        }
    );

    router.route().handler(BodyHandler.create());
    config.getJsonArray(HANDLERS).forEach(handlerDefinition -> {
      JsonObject definition = (JsonObject) handlerDefinition;
      registeredFactories.forEach(factory -> {
        if (factory.getClass().getCanonicalName().equals(definition.getString("class"))) {
          ConfigurableHandler handler = factory.create(definition.getJsonObject("config"));
          handler.register(router, definition.getJsonObject("route"));
          LOG.info("Configured handler with factory: " + factory.getClass() + " and configuration: " + definition);
        }
      });
    });
    router.route().handler(this::handleAll);

    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    LOG.info("Server started on port 8080");
  }

  private JsonObject initConfig() {
    try {
      String config = readFile("/config/application.json", "utf-8");
      return new JsonObject(config);
    } catch (Exception e) {
      LOG.error("Could not load the config file", e);
      throw new RuntimeException(e);
    }
  }

  private String readFile(String path, String encoding) throws IOException {
    InputStream resourceStream = getClass().getResourceAsStream(path);
    StringWriter writer = new StringWriter();
    IOUtils.copy(resourceStream, writer, encoding);
    return writer.toString();
  }

  private void handleAll(RoutingContext routingContext) {
    JsonObject arr = new JsonObject();
    arr.put("messaage", (String) routingContext.get("message"));
    routingContext.response().putHeader("content-type", "application/json")
        .end(arr.encodePrettily());
  }

}
