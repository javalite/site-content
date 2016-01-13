<div class="page-header">
   <h1>Http</h1>
   <h4>Tiny client for web services</h4>
</div>



JavaLite HTTP is a tiny library for making HTTP requests and returning responses. It has no dependencies other than the JDK.

## How to GET

~~~~ {.java}
Get get = Http.get("http://yahoo.com");
System.out.println(get.text());
System.out.println(get.headers());
System.out.println(get.responseCode())
~~~~

## How to POST

~~~~ {.java}
Post post = Http.post("http://yahoo.com", content).header(headerName, headerValue);
System.out.println(post.text());
System.out.println(post.headers());
System.out.println(post.responseCode())
~~~~

## How to PUT and DELETE

Similar to the above.  You can find full JavaDoc here:
<a href="http://javalite.github.io/activejdbc/org/javalite/http/package-summary.html">JavaLite HTTP JavaDoc</a>

## POST content

```java
Post post = Http.post(url, content)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
```

## Basic authentication

```java
String response = Http.get(url).basic(user, password).text();
```

## How to get the dependency

~~~~ {.xml}
<dependency>
    <groupId>org.javalite</groupId>
    <artifactId>javalite-common</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
~~~~

For latest version and  download, refer to [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22javalite-common%22)
