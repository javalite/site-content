Title: Description of !ActiveWeb MVC implementation

-   [Introduction](#Introduction)
-   [Model](#Model)
-   [View](#View)
-   [Controller](#Controller)

Introduction
============

At the heart of the ActiveWeb project there is an MVC pattern - Model-View-Controller. An ActiveWeb project is an implementation of a famous MVC pattern but adopted for the web. In this respect, the ActiveWeb is not any different from a number of other web frameworks, such as Struts 1, Grails, Ruby on Rails, Spring MVC and others.

Model
=====

A model in the ActiveWeb application is represented by custom objects that contain data. These can be models as in [ActiveJDBC](http://code.google.com/p/activejdbc/) models, or any other custom objects which represent information specific to the business domain of the application.

View
====

View in the ActiveWeb project is represented by [FreeMarker](http://freemarker.sourceforge.net/) templates. While ActiveWeb can be used with other templating frameworks, FreeMarker is currently the only implementation. For reasons why FreeMarker was selected, please refer to [FAQ](FAQ). ActiveWeb as a framework allows for better separation of controller and view partly because it is based on [FreeMarker](http://freemarker.sourceforge.net/), thus disallowing creeping scriptlets into views.

Controller
==========

A controller in ActiveWeb is a Java class which extends class `activeweb.AppController and provides one or more public void methods. An instance of such a class is used to process web requests WIKI PARSE WARNING: unterminated backtick!`

At the heart of the ActiveWeb project there is an MVC pattern - Model-View-Controller.

For better understanding of control flow of HTTP request from controllers to views, please refer to FiveMinuteGuideToActiveWeb

For more information on controllers, navigate to ControllerExplained page
