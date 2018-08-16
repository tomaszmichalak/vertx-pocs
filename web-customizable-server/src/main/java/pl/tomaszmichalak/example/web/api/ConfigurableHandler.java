package pl.tomaszmichalak.example.web.api;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public interface ConfigurableHandler extends Handler<RoutingContext> {

  ConfigurableHandler init(Router router, Vertx vertx, JsonObject config);

}
