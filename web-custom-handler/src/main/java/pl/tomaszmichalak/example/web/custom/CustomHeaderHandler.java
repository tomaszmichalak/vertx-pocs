package pl.tomaszmichalak.example.web.custom;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pl.tomaszmichalak.example.web.api.ConfigurableHandler;

public class CustomHeaderHandler implements ConfigurableHandler {

  private String headerValue;

  @Override
  public ConfigurableHandler init(Router router, Vertx vertx, JsonObject config) {
    headerValue = config.getJsonObject("customHeaderHandler").getString("customHeaderValue");
    return this;
  }

  @Override
  public void handle(RoutingContext context) {
    context.response().headers().add("example-custom-header", headerValue);
    context.next();
  }

}
