# ActiveJDBC - Intellij Idea Integration



As a general rule of thumb, instrumentation needs to be performed in case you run a program or a test that will execute ActiveJDBC models.
See [Instrumentation](instrumentation) for more detail. Instrumentation needs to execute after compile and before running your code.

## Configuration


Perform these steps:

* Run -> Edit configurations -> Defaults -> JUnit

* Enter this as a post-Make step:

```
org.javalite:activejdbc-instrumentation:[VERSION]:instrument
```

![Intellij Idea](images/idea_config.png)

> Ensure to enter the latest (or appropriate) version of the library

Since you configured this as a JUnit default configuration, every new JUnit run you create will automatically have this
configuration.

## One-off manual instrumentation

If you just want to execute a one-off instrumentation, simply run it from the Maven tab:

![Intellij Idea manual instrumentation](images/idea_config.png)


## Non-Maven integrations

If you use Ant, simply add an Ant task in the same way as explained above. Same goes for simple command line scripts,
as long as they are executed after Make and before run time.