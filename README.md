# Vert.x PoCs

## Web Customizable Server & Web Stack
With Vert.x-Web you can build web applications with Vert.x. It provides a routing functionality that
allows to register different handlers based on path or HTTP method. The aim of this PoC is to define
configurable list of handlers which can loaded from external jar files.

### Expected logic
We are able to define three REST endpoints:
* [http://localhost:8080/all](http://localhost:8080/all*) that responds with
```json
{
  "messaage" : "All common message!, All custom message!"
}
```
* [http://localhost:8080/custom](http://localhost:8080/custom) that responds with
```json
{
  "messaage" : "Custom message!"
}
```
* [http://localhost:8080/common](http://localhost:8080/common) that responds with
```json
{
  "messaage" : "Common message!"
}
```
Messages `All common message!` and `Common message!` are added via `pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandler` 
handler that is a part of the server module.
Messages `All custom message!` and `Custom message!`are added via `pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandler`
handler that comes from the custom module.

### Modules
The project contains three modules that are assembled to distribution file.

* `web-customizable-server` that contains the server and common handler
* `web-custom-handler` that contains the custom handler
* `web-stack` that contains the configuration file and build the distribution

### Configuration
```json
{
  "handlers": [
    {
      "class": "pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory",
      "route": {
        "method": "GET",
        "pathRegex": "/all*"
      },
      "config": {
        "commonMessage": "All common message!"
      }
    },
    {
      "class": "pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory",
      "route": {
        "method": "GET",
        "pathRegex": "/all*"
      },
      "config": {
        "customMessage": "All custom message!"
      }
    },
    {
      "class": "pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory",
      "route": {
        "method": "GET",
        "path": "/common"
      },
      "config": {
        "commonMessage": "Common message!"
      }
    },
    {
      "class": "pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory",
      "route": {
        "method": "GET",
        "path": "/custom"
      },
      "config": {
        "customMessage": "Custom message!"
      }
    }
  ]
}
```

Note that we do not configure handlers directly. We want to reuse the same handler implementation for
different routes with different configurations so we configure factory classes.

### Build & Run

`web-stack` module aggregates jar files produced via `web-customizable-server` and `web-custom-handler` 
modules with all dependencies. 

So we build the distribution:
```
$> ./gradlew :web-stack:assembleDistributionWithDeps
```
It produces the ZIP file with all dependencies `web-customizable-server/build/distributions/stack.zip`. 
It contains executable jar file - `web-stack.jar` with the manifest: 

```
Manifest-Version: 1.0
Class-Path: web-custom-handler.jar web-customizable-server.jar logback
 -classic-1.0.13.jar vertx-web-3.5.3.jar vertx-auth-common-3.5.3.jar v
 ertx-core-3.5.3.jar logback-core-1.0.13.jar slf4j-api-1.7.5.jar commo
 ns-io-1.3.2.jar netty-codec-http2-4.1.19.Final.jar netty-handler-4.1.
 19.Final.jar netty-handler-proxy-4.1.19.Final.jar netty-resolver-dns-
 4.1.19.Final.jar netty-codec-http-4.1.19.Final.jar netty-codec-socks-
 4.1.19.Final.jar netty-codec-dns-4.1.19.Final.jar netty-codec-4.1.19.
 Final.jar netty-transport-4.1.19.Final.jar netty-buffer-4.1.19.Final.
 jar netty-resolver-4.1.19.Final.jar netty-common-4.1.19.Final.jar jac
 kson-databind-2.9.5.jar jackson-core-2.9.5.jar vertx-bridge-common-3.
 5.3.jar jackson-annotations-2.9.0.jar
Main-Class: pl.tomaszmichalak.example.web.stack.App
```

The unzip the file and run command:

```
$> cd stack/server-stack/
$> java -jar web-stack.jar
```

In the console you should see:
```
INFO: Application config :{"handlers":[{"class":"pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory","route":{"method":"GET","pathRegex":"/all*"},"config":{"commonMessage":"All common message!"}},{"class":"pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory","route":{"method":"GET","pathRegex":"/all*"},"config":{"customMessage":"All custom message!"}},{"class":"pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory","route":{"method":"GET","path":"/common"},"config":{"commonMessage":"Common message!"}},{"class":"pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory","route":{"method":"GET","path":"/custom"},"config":{"customMessage":"Custom message!"}}]}
sie 20, 2018 12:06:28 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Registered handler:pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory
sie 20, 2018 12:06:28 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Registered handler:pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory
sie 20, 2018 12:06:28 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: class pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory and configuration: {"class":"pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory","route":{"method":"GET","pathRegex":"/all*"},"config":{"commonMessage":"All common message!"}}
sie 20, 2018 12:06:28 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: class pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory and configuration: {"class":"pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory","route":{"method":"GET","pathRegex":"/all*"},"config":{"customMessage":"All custom message!"}}
sie 20, 2018 12:06:28 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: class pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory and configuration: {"class":"pl.tomaszmichalak.example.web.common.CommonMessageConfigurableHandlerFactory","route":{"method":"GET","path":"/common"},"config":{"commonMessage":"Common message!"}}
sie 20, 2018 12:06:28 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: class pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory and configuration: {"class":"pl.tomaszmichalak.example.web.custom.CustomMessageConfigurableHandlerFactory","route":{"method":"GET","path":"/custom"},"config":{"customMessage":"Custom message!"}}
...
INFO: Server started on port 8080
```