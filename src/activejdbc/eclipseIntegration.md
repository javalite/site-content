<ol class=breadcrumb>
   <li><a href=/>JavaLite</a></li>
   <li><a href=/activejdbc>ActiveJDBC</a></li>
   <li class=active>Eclipseintegration</li>
</ol>
<div class=page-header>
   <h1>Eclipseintegration <small></small></h1>
</div>





As a general rule of thumb, instrumentation needs to be performed in case you run a program or a test that will execute ActiveJDBC models.
See [Instrumentation](instrumentation) for more detail. Basically Instrumentation adds special byte code instructions for ActiveJDBC to
operate properly. Since Eclipse automatically recompiles classes each time you make modifications and save, it can blow away
instrumented ActiveJDBC classes and replace them with just compiled versions (non-instrumented).

When this happens, you will see an exception similar to:
```
 org.javalite.activejdbc.InitException: failed to determine Model class name, are you sure models have been instrumented?
```
This means that in before you run your program, model classes need to be instrumented.

## Create instrumentation script

This page provides instructions based on a simple Maven ActiveJDBC example. Sources can be found here:
[https://github.com/javalite/simple-example](https://github.com/javalite/simple-example)

Create a directory scripts:

```
mkdir scripts
```

Create instrumentation script:

```
vi ./scripts/instrumentation.sh
```

and place this content into the script:

```
mvn process-classes
```

Since Instrumentation plugin is bound to the `process-classes` phase, it will be executed when this goal is invoked. Make script executable:

```
chmod a+x ./scripts/instrumentation.sh
```

Execute script from the root of project:
```
$./scripts/instrumentation.sh
```

and observe output similar to this:

```
**************************** START INSTRUMENTATION ****************************
Directory: /home/igor/tmp/simple-example/target/classes
Found model: org.javalite.activejdbc.examples.simple.Employee
Instrumented class: org.javalite.activejdbc.examples.simple.Employee in directory: /home/igor/tmp/simple-example/target/classes/
**************************** END INSTRUMENTATION ****************************
```

If you see this output, everything is fine.

## Configure Eclipse builder

Now we need to configure Eclipse Builder to run Instrumentation before executing a unit test or running a program.

Select: Project -> Properties -> Builders, create a new builder and configure it like this:

eclipse-config.png

![Eclipse config](images/eclipse-config.png)


Once this is configured you can run your Java program or a JUnit test. Your Instrumentation builder will be executed in
after Java Builder and Maven Project builder, ensuring that instrumentation is executed just before run time. Eclipse
is also smart to call builders only if there are changes in code.
