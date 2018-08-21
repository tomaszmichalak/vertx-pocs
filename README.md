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
  "messages" : [ {
    "handlerName" : "commonHandler",
    "messageValue" : "All common message."
  }, {
    "handlerName" : "customHandler",
    "messageValue" : "All custom message."
  } ]
}
```
* [http://localhost:8080/custom](http://localhost:8080/custom) that responds with
```json
{
  "messages" : [ {
    "handlerName" : "customHandler",
    "messageValue" : "Custom message."
  } ]
}
```
* [http://localhost:8080/common](http://localhost:8080/common) that responds with
```json
{
  "messages" : [ {
    "handlerName" : "commonHandler",
    "messageValue" : "Common message."
  } ]
}
```
Messages `All common message` and `Common message` are added via `pl.tomaszmichalak.example.web.common.CommonMessageRoutingHandlerFactory.CommonMessageHandler` 
handler that is a part of the server module.
Messages `All custom message` and `Custom message`are added via `pl.tomaszmichalak.example.web.custom.CustomMessageRoutingHandlerFactory.CustomMessageHandler.CustomMessageHandler`
handler that comes from the custom module.

### Modules
The project contains three modules that are assembled to the distribution file.

* `web-customizable-server` that contains the server and common handler
* `web-custom-handler` that contains the custom handler
* `web-stack` that contains the configuration file and build the distribution

### Configuration
```hocon
operations = [
  {
    id = allMessages
    handlers = [
      {
        name = commonHandler
        config.commonMessage = All common message.
      },
      {
        name = customHandler
        config.customMessage = All custom message.
      }
      {
        name = bodyHandler
      }
    ]
  }
  {
    id = customMessages
    handlers = [
      {
        name = customHandler
        config.customMessage = Custom message.
      }
      {
        name = bodyHandler
      }
    ]
  }
  {
    id = commonMessages
    handlers = [
      {
        name = commonHandler
        config.commonMessage = Common message.
      }
      {
        name = bodyHandler
      }
    ]
  }
]
```

The `operation.id` comes from `rest-api.yaml` file that defines Open API specification for our web 
application. So any `GET` request with path `/all`
```yaml
paths:
  /all:
    get:
      summary: List of all messages
      operationId: allMessages
      tags:
        - messages
      responses:
        '200':
          description: Array of messages from customizable routing handlers.
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Messages"
        default:
          description: unexpected error
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
```
is processed by two handlers with names `commonHandler`, `customHandler`. 

We use [ServiceLoader](https://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html) to
load handlers from local and external modules. 

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
 -classic-1.0.13.jar vertx-rx-java2-3.5.3.jar vertx-config-hocon-3.5.3
 .jar vertx-config-3.5.3.jar vertx-web-api-contract-3.5.3.jar vertx-we
 b-3.5.3.jar vertx-auth-common-3.5.3.jar vertx-core-3.5.3.jar logback-
 core-1.0.13.jar json-schema-validator-0.1.13.jar swagger-parser-2.0.0
 -rc3.jar swagger-parser-v2-converter-2.0.0-rc3.jar swagger-compat-spe
 c-parser-1.0.34.jar swagger-parser-1.0.34.jar swagger-parser-v3-2.0.0
 -rc3.jar swagger-core-2.0.0-rc4.jar swagger-core-1.5.18.jar swagger-m
 odels-1.5.18.jar slf4j-ext-1.7.25.jar slf4j-api-1.7.25.jar netty-code
 c-http2-4.1.19.Final.jar netty-handler-4.1.19.Final.jar netty-handler
 -proxy-4.1.19.Final.jar netty-resolver-dns-4.1.19.Final.jar netty-cod
 ec-http-4.1.19.Final.jar netty-codec-socks-4.1.19.Final.jar netty-cod
 ec-dns-4.1.19.Final.jar netty-codec-4.1.19.Final.jar netty-transport-
 4.1.19.Final.jar netty-buffer-4.1.19.Final.jar netty-resolver-4.1.19.
 Final.jar netty-common-4.1.19.Final.jar jackson-dataformat-yaml-2.9.3
 .jar json-patch-1.6.jar json-schema-validator-2.2.8.jar json-schema-c
 ore-1.2.8.jar jackson-coreutils-1.8.jar jackson-databind-2.9.5.jar ja
 ckson-core-2.9.5.jar rxjava-2.1.9.jar reactive-streams-1.0.2.jar conf
 ig-1.3.0.jar vertx-bridge-common-3.5.3.jar commons-io-2.4.jar swagger
 -parser-core-2.0.0-rc3.jar swagger-models-2.0.0-rc4.jar jackson-annot
 ations-2.9.3.jar commons-lang3-3.7.jar httpclient-4.5.2.jar swagger-a
 nnotations-2.0.0-rc4.jar validation-api-1.1.0.Final.jar snakeyaml-1.1
 8.jar uri-template-0.9.jar guava-20.0.jar mailapi-1.4.3.jar joda-time
 -2.9.7.jar libphonenumber-8.0.0.jar msg-simple-1.1.jar btf-1.2.jar js
 r305-3.0.1.jar jopt-simple-5.0.3.jar httpcore-4.4.4.jar commons-loggi
 ng-1.2.jar commons-codec-1.9.jar swagger-annotations-1.5.18.jar rhino
 -1.7R4.jar activation-1.1.jar
Main-Class: pl.tomaszmichalak.example.web.stack.App
```

The unzip the file and run command:

```
$> cd stack/server-stack/
$> java -jar web-stack.jar
```

In the console you should see:
```
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Registered handler:customHandler
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Registered handler:commonHandler
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Registered handler:bodyHandler
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: commonHandler and configuration: {"config":{"commonMessage":"All common message."},"name":"commonHandler"}
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: customHandler and configuration: {"config":{"customMessage":"All custom message."},"name":"customHandler"}
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: bodyHandler and configuration: {"name":"bodyHandler"}
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: customHandler and configuration: {"config":{"customMessage":"Custom message."},"name":"customHandler"}
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: bodyHandler and configuration: {"name":"bodyHandler"}
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: commonHandler and configuration: {"config":{"customMessage":"Common message."},"name":"commonHandler"}
sie 21, 2018 3:18:15 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Configured handler with factory: bodyHandler and configuration: {"name":"bodyHandler"}
15:18:16.502 [vert.x-eventloop-thread-0] DEBUG io.netty.channel.DefaultChannelId - -Dio.netty.processId: 3968 (auto-detected)
15:18:17.444 [vert.x-eventloop-thread-0] DEBUG io.netty.channel.DefaultChannelId - -Dio.netty.machineId: f8:ca:b8:ff:fe:45:5c:2c (auto-detected)
sie 21, 2018 3:18:17 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: The server is started on the port 8080
```