package pl.tomaszmichalak.example.web.common;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import pl.tomaszmichalak.example.web.api.RoutingHandlerFactory;

public class BodyRewriterHandlerFactory implements RoutingHandlerFactory {

  @Override
  public String getName() {
    return "bodyHandler";
  }

  @Override
  public Handler<RoutingContext> create(JsonObject config) {
    return routingContext -> {
      JsonObject arr = new JsonObject();
      arr.put("messages", (JsonArray) routingContext.get("messages"));
      routingContext.response().putHeader("content-type", "application/json")
          .end(arr.encodePrettily());
    };
  }
}
