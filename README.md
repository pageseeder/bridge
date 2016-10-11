[ ![Download](https://api.bintray.com/packages/pageseeder/maven/bridge/images/download.svg) ](https://bintray.com/pageseeder/maven/bridge/_latestVersion)

# PageSeeder Bridge

The PageSeeder Bridge API provides a convenient Java API to interact with the [PageSeeder service API](https://dev.pageseeder.com/api.html).

It includes:
 - low-level classes to connect directly to PageSeeder services using a built-in HTTP and OAuth client.
 - a basic object model that maps to PageSeeder objects
 - SAX handlers and utility classes to automatically parse responses into objects
 - managers for common operations

# Quick start guide

## PageSeeder service

Typical code to call a PageSeeder service:

```java
  PSMember member = null;
  String service = ServicePath.newPath("/members/{member}", username);
  Response response = new Request(GET, service).using(credentials).response();
  if (response.isSuccessful()) {
    // Parse the response into an account
    member = response.consumeItem(handler);
  } else {
    // Collect and log error, etc...
    ServiceError error = response.consumeServiceError();
  }
```

## Via Manager

The same operation as above can be done with a manager albeit with less control over how the returned content or errors are handled:

```java
  MemberManager manager = new MemberManager(credentials);
  PSMember member = manager.getByUsername(username);
```

# Architecture

## `org.pageseeder.bridge.control`

This package provides managers for the PageSeeder objects defined in the `model`package for common operations.

Below are the managers provided in this package:
 - `CommentManager`
 - `DocumentManager`
 - `ExternalURIManager`
 - `GroupManager`
 - `MemberManager`
 - `MembershipManager`
 - `PSSearch`
 - `ThreadManager`
 - `XRefManager`

Although the managers are convenient, in some cases it is preferrable to define a specific object model and invoke the PageSeeder services so that they map to your business objects. 

When possible the managers automatically cache the objects that have been retrieved using EHCache - check your API documentatation. In doing so, the bridge makes some assumptions about how the data will be used and this may not apply to you. For better control of caching, we recommend using the HTTP client directly with your own objects and handlers.

## `org.pageseeder.bridge.http`

A fluent API for HTTP connections to PageSeeder.

### `Request` object

The request is reusable and supports setting and getting parameters and HTTP headers.

HTTP headers can be set using the chainable `request.header(String name, String value)` method. Some headers are set automatically such as `Content-Type` and `Content-Length` and can be queried before the connection is made.

Etags can be set using `header.etag(String etag)`.

Parameters can be set on the request using `request.parameter(String name, String value)` or `request.parameters(Map<String, String> parameters)`.

Credentials are set with the `request.using(PSCredentials credentials)` methods. Credentials can be a user session, OAuth token or username/password using Basic Auth.

Requests can be made on different `PSConfig` instances. If the config is not specified, the request object will use the default config as returned by `PSConfig.getDefault()`.

### `Response` object

The response can be instantiated from a request at any time using the `request.response()` - this method always returns a `Response`. 
 - If the server responds to the request without an I/O or connection error, `response.isAvailable()` returns `true`. 
 - If response is successful (HTTP status code 200,201,202) `response.isSuccessful()` returns `true`.
 - If the response content has an XML mediatype, `response.isXML()` returns `true`.

When the logging level is set to DEBUG for org.pageseeder.bridge.http.Response, the response will print the response content to System.out for successful responses or System.err for errors.
 
 
The response headers are fully available using the `header(String name)` or one of the convenience methods such as `length()`, `charset()`, `mediaType()`.

The session is automatically updated.

The content can be processed by one of the consumer methods to:
 - retrieve the content as a byte array, a String, an object or list of objects
 - process the content using a SAX handler, XML Writer or XSLT

If one of these methods fails, a runtime `ContentException` will be thrown instead of an I/O error. Streams are closed appropriately.

The content of a response is always processed if available.
If the response detects a service error, it will try to build a ServiceError automatically.

The `Response` object implements the [`AutoCloseable`](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html) interface so that responses are automatically closed when they are no longer needed when using the try with statement:
```
try (Response response = request.response()) {
   // Dealing with the response
}
```
The `close()` method will efficiently consume the output of the underlying connection if necessary to ensure that the connection can be persisted by freeing up the underlying socket connection for reuse. Failing to consume the content of a successful response would prevent the connection from being reused.

Note that it isn't strictly necessary to consume the content of a failed HTTP response (code >=400).
For more details, see http://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html

To make it easier to debug the response content. We've introduced a mechanism to copy the response content to `System.out` if successful or `System.err` if the response code is greater than 400.

This can be done programmatically:

```
// Enable content debug will copy to system output 
Response.enableDebug();

// Disable content debug
Response.disableDebug();
```

Or by setting the system property `bridge.http.responseDebug` to `true`.

### Examples

Get the content as a string:
```java
Response response= new Request(Method.GET, Service.get_version).response();
if (response.isSuccessful()) {
   String out = response.consumeString();
}
```

Using a handler on the XML:
```java
Response response= new Request(Method.GET, "/self").response();
if (response.isSuccessful() && response.isXML()) {
   PSMemberHandler handler = new PSMemberHandler();
   response.consumeXML(handler);
   PSMember member = handler.get();
}
```

Copying the XML to the output
```java
Response response= new Request(Method.GET, "/uri/"+id).using(token).response();
if (response.isSuccessful() && response.isXML()) {
   response.consumeXML(xml);
}
```

Retrieving the bytes from a response.
```java
Response response= new Request(Method.GET, "/uri/"+id).using(session).response();
if (response.isSuccessful()) {
   String media  = response.mediaType();
   long length = response.length();
   long modified = response.modified();
   byte[] data = response.consumeBytes();
}
```

Caching
```java
// Retrieve the etag
Response response = new Request(Method.GET, path).response();
if (response.isSuccessful()) {
   String etag = response.etag();
}

// Use the etag
Response response = new Request(Method.GET, path).etag(etag).response();
if (response.code() == 304) {
   // Not modified
} else {
   // Awe snap!
}
```

### `ServicePath` object

The service path is a simple utility applying the URL template rules for PageSeeder, so that paths to services can be constructed easily.

For example:
```java
  ServicePath.newServicePath('/member/{member}').toPath(member);
```

## `org.pageseeder.bridge.model`

Provides a simple object model as POJO to work with PageSeeder objects, including:
 - `PSMember`
 - `PSMembership`
 - `PSGroup`
 - `PSProject`
 - `PSFolder`
 - `PSURI`
 
In some cases, it is preferrable to create your own object model mapping to the PageSeeder concepts.

## `org.pageseeder.bridge.net`

This package contains the network classes and the original HTTP client API for the Bridge usign connectors.

Use the `org.pageseeder.bridge.http` in preference to classes in this package.

## `org.pageseeder.bridge.nio`

Provides a [`FileTypeDetector`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/spi/FileTypeDetector.html) for PSML.

## `org.pageseeder.bridge.oauth`

A simple OAuth and OpenID client for PageSeeder (5.9+ only).

### `AuthorizationRequest`

This class provides a simple mechanism to build the URL to the authorization endpoint as part of the authorization code flow.

Typical usage:
```
  String clientId = "1234abcd1234abcd";

  // Create a new request with the specified client ID
  AuthorizationRequest request = AuthorizationRequest.newAuthorization(clientId);

  // Save the state used in request to compare with response from authorizer
  String state = request.state();
 
  // Use this URL to redirect the client
  String url = request.toURLString();
```

### `TokenRequest`

A request to the PageSeeder token endpoint.

This class provides a factory for all server-side token requests:
 * `authorization_code`
 * `password`
 * `client_credentials`
 * `refresh_token`

Example for authorization code flow:
```
  // Instantiate a new request using code returned from authorization end point
  TokenRequest request = TokenRequest.newAuthorizationCode(code, clientCredentials);
```

Example for resource owner password flow:
```
  // Specify client credentials
  UsernamePassword userCredentials = new UsernamePassword("ali_baba", "open sesame!");
 
  // Instantiate a new request
  TokenRequest request = TokenRequest.newPassword(userCredentials, clientCredentials);
```

Example for client credentials flow:
```
  // Instantiate a new request
  TokenRequest request = TokenRequest.newClientCredentials(clientCredentials);
```

Example for token refresh flow:
```
  // Instantiate a new request using refresh token
  TokenRequest request = TokenRequest.newRefreshToken(refreshToken, clientCredentials);
```

### `TokenResponse`

The response from a token request.

Typical usage:
```
  // Execute the request to get a new response
  TokenResponse response = request.execute();
  if (response.isSuccessful()) {
    PSToken token = response.getToken();

    // If grant type and client supports refresh tokens
    String refreshToken = response.getRefreshToken();

    // If using 'openid profile' scope
     PSMember member = response.getMember();

  } else {
    // Handle error
  }
```

## `org.pageseeder.bridge.psml`

Objects mapping to PSML for editing.

## `org.pageseeder.bridge.spi`

Service Provider Interface to allow automatic loading of the PageSeeder configuration to use within the Bridge.

## `org.pageseeder.bridge.util`

Utility classes and sampler to create PageSeeder object for testing.

## `org.pageseeder.bridge.xml`

Handlers to parse XML data.

### Handlers

The bridge handlers simplify processing of XML data by typical SAX handlers.

HTTP response can process directly the content if supplied a `Handler<T>` which must implement the following methods:
```
  public List<T> list();
  public T get();
```

For example:
```
  List<SomeObject> list = response.consumeList(Handler<SomeObject> handler);
  SomeObject item = response.consumeItem(Handler<SomeObject> handler);
```

The `BasicHandler<T>` and `SimpleHandler<T>` implementations can be extended to simplify the development of handlers as they maintains the state of the parser to access the current, parent and ancestor elements and make it easier to extract text data and generate the list.

### Result handler `BasicResultHandler`

New handler to simplify the construction of objects from PageSeeder search results by extending the basic handler and providing methods to report individual results and fields from the PageSeeder response.

```java
  void startResult(String group);
  void endResult();
  void field(String name, String value);
```

### Field extraction

By default, all index fields values are extracted and reported, which may result in unnecessary string construction and methods for unused field. The list of fields to report can be specified in the constructor.

For example, the code below will only report and extract the value of fields `psid` and `pstitle`.

```java
BasicResultHandler<?> handler = new BasicResultHandler<?>("psid", "pstitle");
```


