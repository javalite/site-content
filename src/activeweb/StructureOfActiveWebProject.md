Title: Learn about the structure of ActiveWeb project

-   [Introduction](#Introduction)
-   [Configuration](#Configuration)
-   [Location of controllers](#Location_of_controllers)
-   [Location of models](#Location_of_models)
-   [Location of services](#Location_of_services)
-   [Location of views](#Location_of_views)
-   [RequestDispatcher configuration](#_RequestDispatcher_configuration)

Introduction
============

ActiveWeb is a Maven project, and as such has all the usual Maven conventions. This allows developers to become productive quickly, since locations of artifacts are the same as for a typical Maven Web.

However, there are some specifics regarding ActiveWeb, which are discussed below.

http://activeweb.googlecode.com/files/aw-structure.png

Configuration
=============

In the image above, you can see that the there is a package `app.config`. This package contains 2 - 3 classes responsible for configuration of your project. ActiveWeb does not have property files, XML or any other text based files for configuration. In fact, it barely has any configuration. Most configuration of the application is dealing with configuring database connections.For more on configuration, see here: [Configuration](Configuration)

Location of controllers
=======================

Controllers are *always* located in a package `app.controllers` (and sub-packages). At first this looks strange, and uncomfortable, but this is a requirement of the framework and a general convention. Developers after a while get used to and start to appreciate it. A controller is a Web component, and as such is not a subject of sharing across multiple applications. This means that controllers of one web applications will not intermingle with controllers of another. This approach yields the following benefits:

-   ActiveWeb application structure is identical from one project to another, making it easier to onboard new developers
-   Controllers are compiled on the fly and reloaded on every request in development mode. This provides for unprecedented speed of development

Location of models
==================

ActiveWeb has a good integration with [ActiveJDBC](http://code.google.com/p/activejdbc/), and the `app.models` package is for ActiveJDBC models. However, while this is a convention and a recommendation, there is no hard requirement on the part of framework to keep models in this package or even in this module. For instance, in some of our projects, we keep models in a different shared module so that we can share a DB layer access across multiple projects.

Location of services
====================

The package `app.services` is a mere suggestion where they need to be located. If a service is not shared across many projects, it makes sense to place it into this package. If a service is used in other places, it migth actually be in a completely different module.

ActiveWeb also provides automatic dependency injection of services into controllers and filters. The chosen DI container for ActiveWeb is [Google Guice](http://code.google.com/p/google-guice/). For more information, please refer to [Dependency injection](DependencyInjection).

Location of views
=================

ActiveWeb uses (currently) [FreeMarker](http://freemarker.sourceforge.net/) for rendering and does not use JSPs. For reasons on this decision, please refer to the [FAQ](FAQ). The FreeMarker templates are located in directory `src/main/webapp/WEB-INF/views`. The subdirectories indicate controller names.

In an example above, there are three directories under `WEB-INF/views`: books, layouts and system

`books` is a directory specific to this small example project. It hosts templates for `app.controllers.BooksController`

`system` is a directory provided by ActiveWeb. It contains two templates which are used by framework. Application developers are free to customize these according to the look and feel of the website.

-   The `404.ftl` is rendered when a resource not found (no controller, or action, or template)
-   the `system.ftl` is used when there is an internal system error (exceptions in controllers, etc.).

`layouts` is a directory to hold default and other layouts. The default layout is called `default_layout.ftl` and is invoked automatically by default (wraps every page into a layout)

RequestDispatcher configuration
===============================

ActiveWeb framework is really a Servlet filter. As a result it is easy to set it up in a file `web.xml`. Below is a configuration file from a real commercial project. Usually these file do not get any more complicated as the one below:

~~~~ {.prettyprint}
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">

    <display-name>activeweb</display-name>

    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>

    <filter>
        <filter-name>dispatcher</filter-name>
        <filter-class>org.javalite.activeweb.RequestDispatcher</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>exclusions</param-name>
            <param-value>css,images,js,html,ico,png</param-value>
        </init-param>
        <init-param>
            <param-name>root_controller</param-name>
            <param-value>home</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>dispatcher</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

</web-app>
~~~~

There are three parameters you can use to configure the framework:

-   **encoding** - this is what encoding to set on HTTP request and response just before using either. We recommend to always set it to UTF-8 to avoid issues with internationalization
-   **exclusions** - this is a comma-separated list of strings on URI which the framework should ignore (not attempt to process are requests to controllers). These are static files, like images, HTML, CSS, JavaScript, PDF downloads, etc.
-   **root\_controller** - this is a name if a controller that the framework will automatically call if no path is provided, such as: http://yourdomain.com. For this specific example, the controller called will be `app.controllers.HomeController` with default action `index`. This is equivalent to calling this URL: 1http://yourdomain.com/home1.

