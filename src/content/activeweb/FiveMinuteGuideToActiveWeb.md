Title: 5 minute guide and the simplest dynamic ActiveWeb application

-   [Introduction](#Introduction)
-   [URL mapping convention](#URL_mapping_convention)
-   [Action mapping convention](#Action_mapping_convention)
-   [View mapping convention](#View_mapping_convention)
-   [Example passing data to view](#Example_passing_data_to_view)

Introduction
============

At the heart of an ActiveWeb application is a controller. A controller is a component whose role is to accept and process an HTTP request. This is similar to a Servlet, or a controller class in SpringMVC, Action class in Struts 1 or a controller in Ruby on Rails or Grails

* * * * *

ActiveWeb is a convention-based framework

* * * * *

Here is an example of the simplest controller:

~~~~ {.prettyprint}
public class GreetingController extends AppController{
   public void index(){}
}
~~~~

URL mapping convention
======================

Controllers are automatically mapped to a URL, such that a controller name is underscored (without the word Controller). Here is an example:

~~~~ {.prettyprint}
http://localhost:8080/testapp/greeting
~~~~

When this URL is accessed, the `GreetingController` is executed. Since no further information is provided on the URL, the `index()` method will be processed. Controller methods processed as a result of an HTTP request are called **actions**.

Action mapping convention
=========================

If the URL contained more information, let's say:

~~~~ {.prettyprint}
http://localhost:8080/testapp/greeting/hello
~~~~

then the system would expect that the controller would have a "hello" action, as in:

~~~~ {.prettyprint}
public class GreetingController extends AppController{
   public void hello(){}
}
~~~~

However, as in the previous example, if the action is omitted, it causes the framework to fall back on a default action "index".

View mapping convention
=======================

In this case, the action `index` does not have any code, the framework will pass control to a view. This view will be looked for under:

~~~~ {.prettyprint}
/WEB-INF/views/greeting/index.ftl
~~~~

Where `/WEB-INF/views/` is a base location for all views, directory "greeting" is named after controller, and a view template "index" is called to render because action "index" of the controller was executed. The content of the `index.ftl` will be displayed in browser.

Example passing data to view
============================

In the graphic below, you see how ActiveWeb routes the request to `GreetingController` and action "hello". The complete sequence of operations:

1.  A URL `http://localhost:8080/simple-example/greeting/hello?name=Bob` is entered into the browser.
2.  The Framework upon receiving the request, routes it to the controller GreetingController, action "hello". The controller passes some data to view - "date" as internally generated data, as well as "name" - HTTP request parameter, accessed with `param("name")` method
3.  The Framework then passes the data to a view template `WEB-INF/views/greeting/hello.ftl` for rendering

[View image in full resolution](http://activeweb.googlecode.com/files/aw.png)

http://activeweb.googlecode.com/files/aw.png

* * * * *

In this example, the code you see on the image above is all that there is. There are no XML files, no property or any other configuration files.

* * * * *
