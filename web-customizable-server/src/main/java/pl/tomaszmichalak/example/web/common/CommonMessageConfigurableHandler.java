package pl.tomaszmichalak.example.web.common;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pl.tomaszmichalak.example.web.api.ConfigurableHandler;

public class CommonMessageConfigurableHandler implements ConfigurableHandler {

  private String message;

  CommonMessageConfigurableHandler(JsonObject config) {
    message = config.getString("commonMessage");
  }

  @Override
  public ConfigurableHandler register(Router router, JsonObject routeConfig) {
    Route route = route(router, routeConfig);
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
