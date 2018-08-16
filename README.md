# Vert.x PoCs

## Web Customizable Server & Web Stack
With Vert.x-Web you can build web applications with Vert.x. It provides a routing functionality that
allows to register different handlers based on path or HTTP method. The aim of this PoC is to define
configurable list of handlers which can loaded from external jar files.

```json
{
  "handlers": [
    "pl.tomaszmichalak.example.web.common.CommonHeaderHandler",
    "pl.tomaszmichalak.example.web.custom.CustomHeaderHandler"
  ]
}
```

The first handler is the part of `web-customizable-server` that contains the main Verticle and exposes 
API with custom handler. The second one is the part of `web-custom-handler` that comes with some custom logic.

### Build & Run

`web-stack` module aggregates jar files produced via `web-customizable-server` and `web-custom-handler` 
modules with all dependencies. So we need to build some kind of distribution and run it as simple jar file.

Build the distribution:
```
$> ./gradlew :web-stack:assembleDistributionWithDeps
```
It produces the ZIP file with all dependencies `web-customizable-server/build/distributions/stack.zip`.

The unzip the file and run command:

```
$> cd stack/server-stack/
$> java -jar web-stack.jar
```

In the console you should see:
```
sie 16, 2018 1:03:48 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Registered handler: pl.tomaszmichalak.example.web.custom.CustomHeaderHandler
sie 16, 2018 1:03:48 PM pl.tomaszmichalak.example.web.ConfigurableServer
INFO: Registered handler: pl.tomaszmichalak.example.web.common.CommonHeaderHandler
...
INFO: Server started on port 8080
```

Then call [http://localhost:8080/hello](http://localhost:8080/hello)

and check response headers:

```
example-common-header: CommonHeaderValue
example-custom-header: CustomHeaderValue
```