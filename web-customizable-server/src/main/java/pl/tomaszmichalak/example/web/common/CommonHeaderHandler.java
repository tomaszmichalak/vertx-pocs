package pl.tomaszmichalak.example.web.common;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pl.tomaszmichalak.example.web.api.ConfigurableHandler;

public class CommonHeaderHandler implements ConfigurableHandler {

  private String headerValue;

  @Override
  public ConfigurableHandler init(Router router, Vertx vertx, JsonObject config) {
    headerValue = config.getJsonObject("commonHeaderHandler").getString("commonHeaderValue");
    return this;
  }

  @Override
  public void handle(RoutingContext context) {
    context.response().headers().add("example-common-header", headerValue);
    context.next();
  }

}
