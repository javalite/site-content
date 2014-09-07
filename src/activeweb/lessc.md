# Less compiler

[Less compiler](http://lesscss.org/) is a better way to write CSS. ActiveWeb provides a built-in mechanism to generate
CSS file on the fly with a use of a Less compiler.

> Less configuration below requires that you install a Less compiler from: [Less compiler](http://lesscss.org/)


Here is how to configure:

First, configure the routes to ignore "/bootstrap.css" route in all environments except development:

~~~~ {.java}
public class RouteConfig extends AbstractRouteConfig {
    public void init(AppContext appContext) {
        ignore("/bootstrap.css").exceptIn("development");
    }
}
~~~~


Then, create a new controller:

~~~~ {.java}
public class BootstrapController extends AbstractLesscController {
    @Override
    protected File getLessFile() {
        return new File("src/main/webapp/less/bootstrap.less");
    }
}
~~~~

And override the `getLessFile()` method to return a location of your main Less file.

Additionally, add a Maven plugin to your pom file:

~~~~ {.xml}
<plugin>
    <groupId>org.javalite</groupId>
    <artifactId>activeweb-lessc-maven-plugin</artifactId>
    <version>1.11-SNAPSHOT</version>
    <configuration>
        <lesscMain>${basedir}/src/main/webapp/less/bootstrap.less</lesscMain>
        <targetDirectory>${basedir}/target/web</targetDirectory>
        <targetFileName>bootstrap.css</targetFileName>
    </configuration>
    <executions>
        <execution>
            <goals><goal>compile</goal></goals>
        </execution>
    </executions>
</plugin>
~~~~

Additionally, configure to package the CSS file into the app with a War plugin:

~~~~ {.xml}
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <version>2.4</version>
    <configuration>
        <webResources>
            <resource>
                <directory>target/web</directory>
            </resource>
        </webResources>
    </configuration>
</plugin>
~~~~ 

As a result, when your application is running in development environment, the `BootstrapController` is compiling CSS
from less files in cases there are changes in the files. It checks for changes and re-compiles if needed on each request.
During the build, the plugin fully compiles all Less files into a single target CSS file, which then gets packaged
into the war file. The file `bootstrap.css` is then served from container, because this path is ignored in any
environment except development. In fact, in a real production system this file like any other static files will
be served by a web server or a CDN.




