Title: Logging configuration

Introduction
============

ActiveJDBC uses [SLF4J](http://www.slf4j.org/) logging facade. Please, refer to SLF4J documentation to see how to configure it with Log4J, Java Logging, Apache logging, etc.

ActiveJDBC logging configuration
================================

ActiveJDBC uses a system property `activejdbc.log` for specifying logging. The value of this property can be:

-   blank - in this case, ActiveJDBC will spit out all available information - every SQL statement, cache hits/misses, cache purge events, etc.
-   regular expression - in this case, ActiveJDBC will only log statements that match a regular expression

If you just want to see all messages from ActiveJDBC, start your program like this:

~~~~ {.prettyprint}
java -Dactivejdbc.log com.acme.YourProgram
~~~~

If you only want to see select messages, you can provide an expression:

~~~~ {.prettyprint}
java -Dactivejdbc.log=select.* com.acme.YourProgram
~~~~

Dynamically change log output
=============================

Use this call:

~~~~ {.prettyprint}
activejdbc.LogFilter.setLogExpression("regular expression goes here");
~~~~

to dynamically change ActiveJDBC log output at run time.

back to [Features](Features)
