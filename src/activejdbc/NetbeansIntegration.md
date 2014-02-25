Title: How to integrate ActiveJDBC instrumentation into Netbeans

Introduction
============

For general questions, refer to [Instrumentation](Instrumentation) page.

There were some questions posted on ActiveJDBC Google Group regarding integrating Netbeans and ActiveJDBC.

I will try to describe the process and bottlenecks for such goal

Details
=======

Create new Netbeans project

Add dependent libraries:

-   Right click on the newly created project -\> Properties -\> Libraries
-   Add "Compile" libraries from [https://activejdbc.googlecode.com/svn/trunk/examples/ant-example/lib/](https://activejdbc.googlecode.com/svn/trunk/examples/ant-example/lib/)
-   Add "Processor" libraries from [https://activejdbc.googlecode.com/svn/trunk/examples/ant-example/build\_time\_libs/](https://activejdbc.googlecode.com/svn/trunk/examples/ant-example/build_time_libs/)
-   Close

Click "Files" tab and open build.xml

Add "-post-compile" target :

~~~~ {.prettyprint}
<target name="-post-compile">
    <java classname="org.javalite.instrumentation.Main" failonerror="true">
        <sysproperty key="outputDirectory" value="${build.classes.dir}"/>
        <classpath>
            <pathelement path="${build.classes.dir}" />
            <pathelement path="${javac.classpath}" />
            <pathelement path="${javac.processorpath}" />
        </classpath>
    </java>
</target>
 
~~~~

-   Save and close the file

If you are running project from Netbeans you need to perform additional step:

-   Right click on the project -\> Properties -\> Compiling and uncheck "Compile on save"

Back to [Features](Features)
