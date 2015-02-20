<ol class=breadcrumb>
   <li><a href=/>JavaLite</a></li>
   <li><a href=/activejdbc>ActiveJDBC</a></li>
   <li class=active>Instrumentation</li>
</ol>
<div class=page-header>
   <h1>Instrumentation <small></small></h1>
</div>



ActiveJDBC requires instrumentation of class files after they are compiled. This is accomplished with an Instrumentation
tool provided by the project. There are three ways to use it: with a Maven plugin, Ant, and as a standalone Java
class (no Ant or Maven)

## What is instrumentation?

Instrumentation is byte code manipulation that happens after compile phase. It adds static methods from super
class to a subclass. Instrumentation allows to "inherit" static methods from a super class, making elegant code like this
possible:

~~~~ {.java}
List<Person> retirees = Person.where("age >= ?", 65);
~~~~

Without instrumentation, ActiveJDBC would not be able to know what table to query. While instrumentation introduces an
additional step in the process, the benefit is a very intuitive and concise API.

## Maven instrumentation plugin

The simple usage of a Maven plugin is provided by a Maven ActiveJDBC Simple Example project: [Simple Mave Example](https://github.com/javalite/simple-example).
Specifically, the plugin is added to a pom like this:

~~~~ {.xml}
<plugin>
    <groupId>org.javalite</groupId>
    <artifactId>activejdbc-instrumentation</artifactId>
    <version>1.4.9</version>
    <executions>
        <execution>
            <phase>process-classes</phase>
            <goals>
                <goal>instrument</goal>
            </goals>
        </execution>
    </executions>
</plugin>
~~~~

and binds to a `process-classes` Maven phase. It will automatically instrument model classes during the build.

## Ant instrumentation

Here is an example project with Ant - based instrumentation: [Ant exampe](https://github.com/javalite/ant-example)

The class responsible for instrumentation is called `org.javalite.activejdbc.instrumentation.Main`, and here is an example of using it:

~~~~ {.xml}
<target name="instrument" depends="compile">
    <java classname="org.javalite.activejdbc.instrumentation.Main">
        <sysproperty key="outputDirectory" value="${classes}"/>
        <classpath refid="build_classpath"/>
    </java>
</target>
~~~~

where `${classes}` represents a directory where class files were compiled.

## Standalone instrumentation

If you are not using Maven or Ant, you can run instrumentation with a command similar to this:

~~~~ {.prettyprint}
java  -cp=$CLASSPATH  -DoutputDirectory=build activejdbc.instrumentation.Main
~~~~

where:

-   $CLASSPATH is your classpath (see the build.xml in the Ant example above for things you will need to have on the classspath)
-   build - is a directory where you compiled all classes in a "compile" step before instrumentation

There is an example of a standalone project which does not use any build tool, except Java itself.
Please follow this link for more information: [Standalone instrumentation example project](https://github.com/javalite/standalone-example)

## Speed of instrumentation

... is very fast - for large projects (50 - 60 models) it takes about 5 - 7 seconds, and for small projects (under 10 models) usually within a second or two.

## Build time classpath

The Instrumentation package is required on the classpath only during instrumentation and not required during runtime. For Maven projects, this is automatic. Even it finds its way to the runtime classpath, it will do no harm except for increasing the size.

## Bare bones Ant script

This Ant script can be used on any project in order to speed up development. The reason we use this script sometimes even on Maven projects is speed.
Maven takes a few seconds to startup, but this barebones script is almost instant. You can hook it into IDE to trigger before executing tests:

~~~~ {.xml}
<?xml version="1.0" encoding="UTF-8"?>
<!-- This script is used for fast instrumentation of the project's models-->
<project default="instrument" basedir=".">
    <property name="out.dir" value="target/test-classes"/>
    <path id="instrument_classpath">
        <pathelement location="${out.dir}"/>
        <path location="${user.home}/.m2/repository/org/javalite/activejdbc-instrumentation/1.4.9/activejdbc-instrumentation-1.4.9.jar"/>
        <path location="${user.home}/.m2/repository/javassist/javassist/3.8.0.GA/javassist-3.8.0.GA.jar"/>
        <path location="${user.home}/.m2/repository/org/javalite/activejdbc/1.4.9/activejdbc-1.4.9.jar"/>
    </path>
    <target name="instrument">
        <java classname="org.javalite.instrumentation.Main">
            <sysproperty key="outputDirectory" value="${out.dir}"/>
            <classpath refid="instrument_classpath"/>
        </java>
    </target>
</project>

~~~~

Replace versions with the most up-to-date: [Maven search for ActiveJDBC](http://search.maven.org/#search%7Cga%7C1%7Cactivejdbc)

## IDE Integrations

* [Eclipse Integration](eclipse_integration)
* [Netbeans Integration](netbeans_integration)
