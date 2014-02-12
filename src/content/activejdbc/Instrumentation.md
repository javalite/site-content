Title: ActiveJDBC instrumentation explained

-   [Introduction](#Introduction)
-   [What is instrumentation?](#What_is_instrumentation_)
-   [Maven instrumentation plugin](#Maven_instrumentation_plugin)
-   [Ant instrumentation](#Ant_instrumentation)
-   [Standalone instrumentation](#Standalone_instrumentation)
-   [Speed of instrumentation](#Speed_of_instrumentation)
-   [Build time classpath](#Build_time_classpath)
-   [Bare bones Ant script](#Bare_bones_Ant_script)
-   [Eclipse integration](#Eclipse_integration)
-   [Netbeans integration](#Netbeans_integration)

Introduction
============

ActiveJDBC requires instrumentation of class files after they are compiled. This is accomplished with an Instrumentation package provided by the project. There are three ways to use it: with a Maven plugin, Ant, and as a standalone Java class (in case you have a non-Maven project)

What is instrumentation?
========================

Instrumentation is byte code manipulation that happens after compile phase. It adds static methods from super class to a subclass. Instrumentation makes writing code like this possible:

~~~~ {.prettyprint}
List<Person> retirees = Person.where("age >= ?", 65);
~~~~

Without instrumentation, AJ would not be able to know what table to query. This is one reason why other Java ORM APIs are clunky and require a third party class, such as PersistentManager(JPA), Session (Hibernate), etc.

While instrumentation introduces an additional step in the process, the benefit is a very intuitive and concise API.

Maven instrumentation plugin
============================

The simple usage of a Maven plugin is provided by a Maven ActiveJDBC Simple Example project:

https://activejdbc.googlecode.com/svn/trunk/examples/simple-example/ Specifically, the plugin is added to a pom like this:

~~~~ {.prettyprint}
           <plugin>
                <groupId>activejdbc</groupId>
                <artifactId>activejdbc-instrumentation</artifactId>
                <version>1.4.1</version>
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

and as you can see, binds to a "process-classes" phase. It will automatically instrument your classes during the build.

Ant instrumentation
===================

Instrumenting models with Ant projects is easy too: https://activejdbc.googlecode.com/svn/trunk/examples/ant-example/

The class responsible for instrumentation is called `activejdbc.instrumentation.Main`, and here is an example of using it:

~~~~ {.prettyprint}
    <target name="instrument" depends="compile">
        <java classname="activejdbc.instrumentation.Main">
            <sysproperty key="outputDirectory" value="${classes}"/>
            <classpath refid="build_classpath"/>
        </java>
    </target>
~~~~

where \${classes} represents a directory where class files were compiled.

Standalone instrumentation
==========================

If you are not using Maven or Ant, you can run instrumentation with a command similar to this:

~~~~ {.prettyprint}
java  -cp=$CLASSPATH  -DoutputDirectory=build activejdbc.instrumentation.Main
~~~~

where:

-   \$CLASSPATH is your classpath (see the build.xml) in the Ant example above for things you will need to have on the classspath.
-   build - is a directory where you compiled all classes in a "compile" step before instrumentation

There is an example of a standalone project which does not use any build tool, except Java itself. Please follow this link for more information: https://activejdbc.googlecode.com/svn/trunk/examples/standalone-example/

Speed of instrumentation
========================

... is very fast - for large projects (50 - 60 models) it takes about 5 - 7 seconds, and for small projects (under 10 models) usually within a second or two.

Build time classpath
====================

The Instrumentation package is required on the classpath only during instrumentation and not required during runtime. For Maven projects, this is automatic. Even it finds its way to the runtime classpath, it will do no harm except for increasing the size.

Bare bones Ant script
=====================

This Ant script can be used on any project in order to speed up development. The reason we use this script even on Maven projects is speed. Maven takes time to startup, but this barebones script is almost immediate. You can hook it into IDE to trigger before executing tests:

~~~~ {.prettyprint}
<?xml version="1.0" encoding="UTF-8"?>
<project name="C3" default="instrument" basedir=".">

    <property name="out.dir" value="target/classes"/>

    <path id="instrument_classpath">
        <pathelement location="${out.dir}"/>
        <path location="${user.home}/.m2/repository/org/javalite/activejdbc-instrumentation/1.2.2/activejdbc-instrumentation-1.2.2.jar"/>
        <path location="${user.home}/.m2/repository/javassist/javassist/3.8.0.GA/javassist-3.8.0.GA.jar"/>
        <path location="${user.home}/.m2/repository/org/javalite/activejdbc/1.2.2/activejdbc-1.2.2.jar"/>
    </path>

    <target name="instrument">
        <java classname="org.javalite.instrumentation.Main">
            <sysproperty key="outputDirectory" value="${out.dir}"/>
            <classpath refid="instrument_classpath"/>
        </java>
    </target>

</project>
~~~~

Eclipse integration
===================

[EclipseIntegration](EclipseIntegration)

Netbeans integration
====================

Please, see [NetbeansIntegration](NetbeansIntegration)

back to [Features](Features)
