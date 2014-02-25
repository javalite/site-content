Title: Controller filters explained

[Introduction](#Introduction)

[Filter configuration](#Filter_configuration)

-   [Adding global filters](#Adding_global_filters)
-   [Adding global filters to all controllers, except some](#Adding_global_filters_to_all_controllers__except_some)
-   [Adding controller filters to specific controllers](#Adding_controller_filters_to_specific_controllers)
-   [Adding filters to specific actions](#Adding_filters_to_specific_actions)

[Exception handling](#Exception_handling)

[Out of the box filters](#Out_of_the_box_filters)

-   [DBConnectionFilter](#DBConnectionFilter)
-   [TimingFilter](#_TimingFilter)
-   [RequestPropertiesLogFilter](#_RequestPropertiesLogFilter)
-   [RequestParamsLogFilter](#_RequestParamsLogFilter)
-   [HeadersLogFilter](#_HeadersLogFilter)
-   [How to change log level of provided filters on the fly](#How_to_change_log_level_of_provided_filters_on_the_fly)

Introduction
============

Controller filters are similar to that of Servlet filters, but designed to wrap execution of controllers. They can be used for many tasks that need to trigger before and after execution of a controller, such as login in, loggin, opening a DB connection, timing, etc. Controller filters are implementation of a [Chain of responsibility](http://en.wikipedia.org/wiki/Chain-of-responsibility_pattern) design pattern.

Filters are almost as powerful as controllers. They can inspect any aspects of a request, including request parameters, headers, etc. They can also pre-empt controllers and send different responses than a controller (think of a permission access filter for example, which will redirect to a login screen in case there is an attempt to access a protected resource).

All filters implement this interface:

~~~~ {.prettyprint}
package activeweb;
public interface ControllerFilter {
    void before();
    void after();
    void onException(Exception e);
}
~~~~

Filter configuration
====================

Configuration of filters is done in a class called `app.config.AppControllerConfig`, which needs to extend `activeweb.AbstractControllerConfig`. This class provides ways to bind filters to controllers. It has coarse grain methods for binding as well as fine grained.

* * * * *

Filters before() methods are executed in the same order as filters are registered.

* * * * *

Adding global filters
---------------------

Adding a global filter adds it to all controllers. It makes sense to use this to add timing filters, logging filters, etc.

~~~~ {.prettyprint}
public class AppControllerConfig{
  public void init(AppContext context) {
    addGlobalFilters(new TimingFilter());
  }

}
~~~~

Adding global filters to all controllers, except some
-----------------------------------------------------

In some cases, you need to add filters to all controllers, except a few. For example, you might have a security filter, and there is no point to add it to non-secure controllers, or you have a DBConnectionFilter, and you do not want to open connections for controllers which you know will not use a DB connection (expensive resource). Then you can exclude some controllers from global filters:

~~~~ {.prettyprint}
public class AppControllerConfig{
  public void init(AppContext context) {
    addGlobalFilters(new TimingFilter());
    addGlobalFilters(new DBConnectionFilter()).exceptFor(HomeController.class);
  }

}
~~~~

The `exceptFor` method, takes a vararg, so you can pass multiple controllers there.

Adding controller filters to specific controllers
-------------------------------------------------

To add filters to specific controllers:

~~~~ {.prettyprint}
public class AppControllerConfig{
  public void init(AppContext context) {
    add(new TimingFilter()).to(HomeController.class);
  }
}
~~~~

Both the "add()" an the "to()" methods take in varargs, allowing to bind multiple filters to multiple controllers in one line of code.

* * * * *

Filters' after() methods are executed in the opposite order as filters are registered.

* * * * *

Adding filters to specific actions
----------------------------------

Here is an example of adding a filter to specific actions:

~~~~ {.prettyprint}
public class AppControllerConfig{
  public void init(AppContext context) {
    add(new TimingFilter(), new DBConnectionFilter()).to(PostsController.class).forActions("index", "show");
  }
}
~~~~

Exception handling
==================

The `void onException(Exception e);` method can be used to handle exceptions occurred during execution of a controller of other (inner) filters. It is typical on a project to register a "catch all filter" as a global top-most filter. You probably saw default error page coming from the application server in cases when there is a failure in the application. If you declare a "catch all " filter, this can be avoided, and users would see a friendly page with a message.

Here is an example:

~~~~ {.prettyprint}
public class CatchAllFilter extends HttpSupportFilter {
    public void onException(Exception e) {
        logError(e.toString(), e);
        render("/system/error", Collections.map("message", "Apologies for inconvenience");
    }
}
~~~~

In the code snippet above, the `CatchAllFilter` will be given a chance to log an exception to a log system, but then also to display a friendly styled error page in layout.

Out of the box filters
======================

ActiveWeb provides a number of filters for easy configuration of projects.

DBConnectionFilter
------------------

DBConnectionFilter opens a connection before execution of a controller and closes it after execution. Here is an example of usage of this filter from Kitchensink project:

~~~~ {.prettyprint}
public class AppControllerConfig extends AbstractControllerConfig {
    public void init(AppContext context) {
        add(new DBConnectionFilter()).to(PostsController.class, RpostsController.class);
    }
}
~~~~

In the example above, this filter is attached only to controllers PostsController and RpostsController, presumably other controllers do not require a DB connection. If you use [ActiveJDBC](http://code.google.com/p/activejdbc/) for persistence layer, you do not need to do anything else. If you just want to get access to the underlying DB connection, you can do this inside a controller or inner filter:

~~~~ {.prettyprint}
java.sql.Connection connection = Base.connection();
~~~~

which gives you a full control over this connection.

TimingFilter
------------

Timing filter times how long a request takes to process and logs this to a logging system inside its `after()` method.

It is best to have a timing filter to be registered as a global filter:

~~~~ {.prettyprint}
public class AppControllerConfig extends AbstractControllerConfig {
    public void init(AppContext context) {
        addGlobalFilters(new TimingFilter());
        //..register other filters
    }
}
~~~~

Example output from `TimingFilter`:

~~~~ {.prettyprint}
52248 [920503681@qtp-1457284258-3] INFO activeweb.controller_filters.TimingFilter - Processed request in: 14 milliseconds
~~~~

RequestPropertiesLogFilter
--------------------------

This filter will log properties of a request to a log system. It is useful for debugging. Example output of this filter:

~~~~ {.prettyprint}
32644 [2132827533@qtp-1457284258-0] INFO activeweb.controller_filters.RequestPropertiesLogFilter - 
Request URL: http://localhost:8080/kitchensink/
ContextPath: /kitchensink
Query String: null
URI Full Path: /kitchensink/
URI Path: /
Method: GET
~~~~

RequestParamsLogFilter
----------------------

This filter will log parameters of the request, here is an example:

~~~~ {.prettyprint}
176575 [2090322800@qtp-1699024671-2] INFO activeweb.controller_filters.RequestParamsLogFilter - 
Param: content=content to be determined...
Param: id=3
Param: author=Igor Polevoy
Param: title=What good for Ruby is good for Java: JSpec
~~~~

HeadersLogFilter
----------------

This filter will dump all HTTP request headers:

~~~~ {.prettyprint}
176576 [2090322800@qtp-1699024671-2] INFO activeweb.controller_filters.HeadersLogFilter - 
Header: Accept-Language=en-us,en;q=0.5
Header: Cookie=JSESSIONID=6trloxem6xib; remember_me=3f654b9f-8abd-4693-bf62-43ccc7c6
Header: Host=localhost:8080
Header: Content-Length=106
Header: Accept-Charset=ISO-8859-1,utf-8;q=0.7,*;q=0.7
Header: Referer=http://localhost:8080/kitchensink/posts/edit_post/3
Header: Accept-Encoding=gzip,deflate
Header: Keep-Alive=115
Header: User-Agent=Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.16) Gecko/20110323 Ubuntu/10.10 (maverick) Firefox/3.6.16
Header: Content-Type=application/x-www-form-urlencoded
Header: Connection=keep-alive
Header: Accept=text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
~~~~

How to change log level of provided filters on the fly
------------------------------------------------------

You can add a filter to AppContext before registration:

~~~~ {.prettyprint}
public class AppControllerConfig extends AbstractControllerConfig {

    public void init(AppContext context) {

        HeadersLogFilter headersLogger = new HeadersLogFilter();
        context.set("headersLogger", headersLogger);
        
        addGlobalFilters(new TimingFilter(), new RequestPropertiesLogFilter(), new RequestParamsLogFilter(),
                headersLogger);
    }
}
~~~~

This will ensure that you can get to this filter from any controller:

~~~~ {.prettyprint}
public class AdminController extends AppController {

    public void setHeadersLogLevel(){
        //how to disable logging of headers at run time:
        appContext().get("headersLogger", HeadersLogFilter.class).logAtLevel(Level.valueOf(param("log_level")));
    }
}
~~~~

Moving log level of these filters above or below current log system log level is easy and very useful. In production you might want to have these at DEBUG level, but you might want to temporarily enable logging to trace some problem, then turn it off again, all without having to redeploy or restart a server.
