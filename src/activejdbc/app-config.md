<div class="page-header">
   <h1>AppConfig </h1> 
   <h4>configuration library for Java apps</h4>
</div>


AppConfig is a small application configuration library to provide properties for applications deployed to different environments.

## Usage

First, do a static `import AppConfig.p(...)` method:

~~~~ {.java  .numberLines}
import static org.javalite.app_configAppConfig.p;
~~~~ 

then, simply call a statis `p(..)` method in places where you need to inject a property:

~~~~ {.java  .numberLines}
String name = p("name");
~~~~


## Usage

AppConfig allows configuration of applications that is specific for different deployment environments. Applications could have 
environment-specific files, whose names follow this pattern: environment.properties, where environment is a name of a 
deployment environment, such as development, staging, production, etc.

You can also provide a global file, properties from which will be loaded in all environments: global.properties.

> In all cases the files need to be on the classpath under directory/package `/app_config`.

Environment-specific file will have an "environment" part of the file name match to an environment 
variable called `ACTIVE_ENV`. Such configuration is easy to achieve in Unix shell:

~~~~ {.java  .numberLines}
export ACTIVE_ENV=test
~~~~


Typical file structure

```
/app_config
        |
        +--global.properties
        |
        +--development.properties
        |
        +--staging.properties
        |
        +--production.properties
        
```

Global property file will always be loaded, while others will be loaded depending on the value of ACTIVE_ENV environment variable.


> If environment variable `ACTIVE_ENV` is missing, it defaults to `development`.

## System property override

You can also provide an environment as a system property app_config.properties. 

Here is an example (add this to the startup script for your app): 

```
-Dapp_config.properties=/opt/project1/production.properties
```

The `app_config.properties` system property points to a file specific to that computer (local box, server, etc.). 
If a specific property is provided in the properties file loaded on classpath, and the same property is also found in 
 the file `app_config.properties`, then the value loaded from a local file overrides the one loaded from classpath.
  
  
  
