package pl.tomaszmichalak.example.web;


import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import pl.tomaszmichalak.example.web.api.RoutingHandlerFactory;


public class ConfigurableServer extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableServer.class);


  @Override
  public void start() {
    ConfigStoreOptions storeOptions = new ConfigStoreOptions()
        .setType("file")
        .setFormat("hocon")
        .setConfig(new JsonObject().put("path", "application.conf"));

    ConfigRetriever retriever = ConfigRetriever
        .create(vertx, new ConfigRetrieverOptions().addStore(storeOptions));

    retriever.rxGetConfig()
        .flatMap(config ->
            OpenAPI3RouterFactory.rxCreate(vertx, "/rest-api.yaml")
                .doOnSuccess(routerFactory -> {
                  List<RoutingHandlerFactory> routingFactories = getRoutingHandlerFactories();
                  // register handlers
                  config.getJsonArray("operations").forEach(operation ->
                      registerHandlers(routerFactory, (JsonObject) operation, routingFactories)
                  );
                }).flatMap(routerFactory -> vertx.createHttpServer()
                .requestHandler(routerFactory.getRouter()::accept)
                .rxListen(8080))
        ).subscribe(onSuccess -> LOG.info("The server is started on the port 8080"),
        onError -> LOG.error("Could not start the server!", onError));
  }

  private void registerHandlers(OpenAPI3RouterFactory routerFactory, JsonObject operationData,
      List<RoutingHandlerFactory> routingFactories) {
    String operationId = operationData.getString("id");
    operationData.getJsonArray("handlers").forEach(handler -> {
      JsonObject handlerData = (JsonObject) handler;
      routingFactories.forEach(handlerFactory -> {
            if (handlerFactory.getName().equals(handlerData.getString("name"))) {
              routerFactory.addHandlerByOperationId(operationId,
                  handlerFactory.create(handlerData.getJsonObject("config")));
              LOG.info(
                  "Configured handler with factory: " + handlerFactory.getName()
                      + " and configuration: "
                      + handlerData);
            }
          }
      );
    });
  }

  private List<RoutingHandlerFactory> getRoutingHandlerFactories() {
    List<RoutingHandlerFactory> routingFactories = new ArrayList<>();
    ServiceLoader.load(RoutingHandlerFactory.class).iterator().forEachRemaining(factory -> {
          routingFactories.add(factory);
          LOG.info("Registered handler:" + factory.getName());
        }
    );
    return routingFactories;
  }

}
