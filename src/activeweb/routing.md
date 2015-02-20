<ol class=breadcrumb>
   <li><a href=/>JavaLite</a></li>
   <li><a href=/activeweb>ActiveWeb</a></li>
   <li class=active>Routing</li>
</ol>
<div class=page-header>
   <h1>Routing <small></small></h1>
</div>

Routing in ActiveWeb is an act of matching an incoming request URL to a controller and action. Current implementation
supports built-in standard routing, built-in REST - based routing as well as custom routing.

## Standard routing


NOTE: the "context" in all URIs is a web application context, which is usually a WAR file name.

+--------------------------+----------------------------------+------------+------+
|  path                    |   controller                     |     action |   id |
+==========================+==================================+============+======+
|  /books                     app.controllers.BooksController     index           |
+--------------------------+----------------------------------+------------+------+
|  /books/save                app.controllers.BooksController     save            |
+--------------------------+----------------------------------+------------+------+
|  /books/save/123            app.controllers.BooksController     save        123 |
+--------------------------+----------------------------------+------------+------+

In standard routing, the HTTP method is not considered, but you might get an exception of you send an HTTP method to an action that is configured for a different HTTP method. Routing and action HTTP methods are independent in case of standard routing. For standard routing, there is no need to do anything, it works by default

## RESTful routing


In case of restful routing, the actions are pre-configured. RESTful routing is configured by placing a @RESTfull annotation on a controller.
For more informaiton, see: [RESTful controllers](controllers#restful-controllers)

+------------------+----------------------+---------------------------------+------------+----------------------------------------------+
|  HTTP method     |   path               |   controller                    |    action  |    used for                                  |
+==================+======================+=================================+============+==============================================+
|  GET             | /books               | app.controllers.BooksController |  index     |   display a list of all books                |
+------------------+----------------------+---------------------------------+------------+----------------------------------------------+
|  GET             | /books/new\_form     | app.controllers.BooksController |  new_form  |  return an HTML form for creating a new book |
+------------------+----------------------+---------------------------------+------------+----------------------------------------------+
|  POST            | /books               | app.controllers.BooksController |  create    |   create a new book                          |
+------------------+----------------------+---------------------------------+------------+----------------------------------------------+
|  GET             | /books/id            | app.controllers.BooksController |  show      |   display a specific book                    |
+------------------+----------------------+---------------------------------+------------+----------------------------------------------+
|  GET             | /books/id/edit\_form | app.controllers.BooksController |  edit_form |  return an HTML form for editing a books     |
+------------------+----------------------+---------------------------------+------------+----------------------------------------------+
|  PUT             | /books/id            | app.controllers.BooksController |  update    |   update a specific book                     |
+------------------+----------------------+---------------------------------+------------+----------------------------------------------+
| DELETE           | /books/id            | app.controllers.BooksController |  destroy   |   delete a specific book                     |
+------------------+----------------------+---------------------------------+------------+----------------------------------------------+


## Routing with packages

While `app.controllers` is a default package for controllers, you might want to organize them into sub-packages. These sub-packages can only be children of `app.controllers` package though. In case a controller is located in a sub-packages, the path mapping would also include sub-package names:

Standard routing

+--------------------------------------------+---------------------------------------------------+----------+---------+
|  path                                      | controller                                        | action   |id       |
+============================================+===================================================+==========+=========+
| /package1/books                              app.controllers.package1.BooksController            index              |
+--------------------------------------------+---------------------------------------------------+----------+---------+
| /package1/books/save                         app.controllers.package1.BooksController            save               |
+--------------------------------------------+---------------------------------------------------+----------+---------+
| /package1/books/save/123                     app.controllers.package1.BooksController            save         123   |
+--------------------------------------------+---------------------------------------------------+----------+---------+
| /package1/package2/books                     app.controllers.package1.package2.BooksController   index              |
+--------------------------------------------+---------------------------------------------------+----------+---------+
| /package1/package2/books/save                app.controllers.package1.package2.BooksController   save               |
+--------------------------------------------+---------------------------------------------------+----------+---------+
| /package1/package2/books/save/123            app.controllers.package1.package2.BooksController   save         123   |
+--------------------------------------------+---------------------------------------------------+----------+---------+


RESTful routing supports sub-packaging exactly the same as standard.

## Mapping paths to controller names

When matching a path to a controller class, ActiveWeb converts a name of a controller from underscore or hyphenated format to CamelCase:

  path                       controller
  -----------------------   ------------------------------------------------
  /books                     app.controllers.package1.BooksController
  /student_books             app.controllers.package1.StudentBooksController
  /student-books             app.controllers.package1.StudentBooksController



Custom routing
--------------

Besides standard and RESTful, ActiveWeb also offers custom routing. Custom routing provides ability to configure custom URIs to be forwarded to specific controllers and actions.

### Custom routing configuration

As with any other types of configuration, ActiveWeb route configuration is done in code, rather that property or XML files.
Custom routing is done by adding a new class to the application: `app.config.RouteConfig`:

~~~~ {.java}
public class RouteConfig extends AbstractRouteConfig {
    public void init(AppContext appContext) {
        route("/myposts").to(PostsController.class);
        route("/{action}/{controller}/{id}");
        route("/{action}/greeting/{name}").to(HelloController.class);
    }
}
~~~~

Custom routing is based on URI segments, which are chunks of URIs submitted of the request separated by slashes.
For example the following URI has three segments:

~~~~ {.prettyprint}
/greeting/show/bob
~~~~

The above URI has three segments: "greeting", "show", "bob".

ActiveWeb defines three types of segments:

-   Built in
-   Static
-   User (or dynamic)

### Built-in segments

ActiveWeb defines three built-in segments:

-   `{controller}`
-   `{action}`
-   `{id}`

Using built-in segments, you can reorder where controller, action and Id appear on the URI:

~~~~ {.prettyprint}
/(action)/{controller}/{id}
~~~~

When such a route is specified, this URI:

~~~~ {.prettyprint}
/show/photo/123
~~~~

Will be routed to: `app.controllers.PhotoController#show` with ID ==123.

### Static segments

Static segments are simply plain text without the braces. The are matched one to one with the incoming request. Example:

~~~~ {.java}
route("/{action}/greeting/{name}").to(HelloController.class);
~~~~

In the snippet above, "greeting" is a static segment.

### User/dynamic segments

User segments are any text in braces in configuration which are then converted to parameters that can be retrieved inside controllers and filters. Here is an example:

~~~~ {.java}
route("/{action}/greeting/{name}").to(HelloController.class);
~~~~

where "name" is a placeholder whose value will be available in controller:

URL submitted:

~~~~ {.prettyprint}
/show/greeting/alex
~~~~

will be routed to controller `app.controllers.HelloController#show` and value `name` will be available:

~~~~ {.java}
public class HelloController extends AppController{
  public void show(){
    String name = param("name");
  }
}
~~~~

### Wild card routing

Sometimes you need to route a really long URI to a controller. Here is how:

~~~~ {.java}
route("/blog/*items").to(PostsController.class).action("index");
~~~~

In a case the following URL is submitted:

~~~~ {.prettyprint}
/blog/2014/07/23/how-to-define-activeweb-routes
~~~~

it will be routed to controller `app.controllers.PostsController#index` and value `items` will be available:

~~~~ {.java}
public class PostsController extends AppController{
  public void index(){
    logInfo(param("items")); // will print: 2014/07/23/how-to-define-activeweb-routes
  }
}
~~~~

### Http method - based routing

You can include an Http method used in the request into the routing rule:

~~~~ {.java}
route("/{action}/greeting/{name}").to(HelloController.class).get();
~~~~

In this example, this route will only match the incoming request if the Http method of the request is GET.
There are four corresponding methods: `get()`, `post()`, `put()` and `delete()`.
They can be used in isolation or in combination. For instance, this route:

~~~~ {.java}
route("/{action}/greeting").to(HelloController.class).get().post();
~~~~

will match these requests:

GET:

~~~~ {.prettyprint}
/show/greeting
~~~~

POST:

~~~~ {.prettyprint}
/save/greeting
~~~~

Of course the action `save()` needs to have a @POST annotation for this to work. Annotations are independent of routing rules.

> Default Http method used in routing rules is `get()`.

### RouteConfig reloaded

The class `app.config.RouteConfig` is recompiled and reloaded in development environment in case a system property
`active_reload` is set to true. This makes it easy and fun to play with the routes during development. Please see
[Running in development mode](running_in_development_mode) for more information.


## Excluding some routes

Exclusion of some routes is necessary for most applications. Static content, such as CSS, HTML, images, etc. should not be
  processed by the framework, but rather should be served by container directly.
  Please, see the "exclusions" section  of the filter configuration in: [RequestDispatcher configuration](structure_of_activeweb_project#requestdispatcher-configuration)
  for more information.

## Ignoring some routes

Exclusions mechanism described above is sometimes too crude. Lets say we want to dynamically compile Less files into CSS
in development environment, but also want the same URL be served directly from container in any environment except development.

Here is an example configuration:

~~~~ {.java}
public class RouteConfig extends AbstractRouteConfig {
    public void init(AppContext appContext) {
        ignore("/bootstrap.css").exceptIn("development");
    }
}
~~~~
