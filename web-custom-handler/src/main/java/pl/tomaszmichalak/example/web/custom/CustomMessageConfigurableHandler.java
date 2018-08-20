package pl.tomaszmichalak.example.web.custom;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pl.tomaszmichalak.example.web.api.ConfigurableHandler;

public class CustomMessageConfigurableHandler implements ConfigurableHandler {

  private String message;

  CustomMessageConfigurableHandler(JsonObject config) {
    message = config.getString("customMessage");
  }

  @Override
  public ConfigurableHandler register(Router router, JsonObject config) {
    Route route = route(router, config);
    route.handler(this);
    return this;
  }

  @Override
  public void handle(RoutingContext context) {
    String message = context.get("message");
    context.put("message", message == null ? this.message : message + ", " + this.message);
    context.next();
  }

}
