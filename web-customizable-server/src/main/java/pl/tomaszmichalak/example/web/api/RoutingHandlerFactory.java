package pl.tomaszmichalak.example.web.api;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public interface RoutingHandlerFactory {

  String getName();

  Handler<RoutingContext> create(JsonObject config);

}
