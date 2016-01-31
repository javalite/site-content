<div class="page-header">
   <h1>Database migrations</h1>
</div>


Database migrations is a process of making changes to database schema during a development process.
See <a href="http://en.wikipedia.org/wiki/Schema_migration">Schema_migration</a> to understand better what database migrations are.

## DB-Migrator is Maven plugin

Current implementation of this project is a Maven Plugin.
Future releases might include a standalone library for non-Maven projects.

## How to use

Generate a new migration:

~~~~ {.prettyprint}
mvn db-migrator:new -Dname=create_people_table
~~~~

will result in:

~~~~ {.prettyprint}
...
[INFO] Created new migration: src/migrations/20140211113507_create_people_table.sql
...
~~~~

This creates an empty file. Go ahead and add raw SQL to the file

~~~~ {.prettyprint}
create table people ( name varchar (10));
~~~~

Run migration:

~~~~ {.prettyprint}
mvn db-migrator:migrate
...
[INFO] Migrating jdbc:mysql://localhost/test_project using migrations at src/migrations/
[INFO] Migrating database, applying 1 migration(s)
[INFO] Running migration 20140211113507_create_people_table.sql
~~~~

Alternatively, you can just run the build.

## All goals

You can execute plugin help goal to get all information on all other goals:

~~~~ {.prettyprint}
mvn  db-migrator:help
...
[INFO] db-migrator:drop
[INFO]   drops database configured in pom
[INFO] db-migrator:create
[INFO]   creates database configured in pom
[INFO] db-migrator:new
[INFO]   creates a new migration file
[INFO] db-migrator:check
[INFO]   checks that no pending migrations remain. This can be used in build lifecycle to fail the build if pending migrations are found
[INFO] db-migrator:migrate
[INFO]   migrates all pending migrations
[INFO] db-migrator:validate
[INFO]   validates and prints a report listing pending migrations
[INFO] db-migrator:reset
[INFO]   drops/re-creates the database, and runs all migrations, effectively resetting database to pristine state
[INFO] db-migrator:help
[INFO]   prints this message
~~~~


## Where to get

Generally, just add a plugin configuration to your pom, as described below. If you want to download, you can
do so here: <a href="http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22db-migrator-maven-plugin%22">db-migrator-maven-plugin</a>


## Property file configuration

> Using a property file for connection configuration is a preferred way of configuring JavaLite DB-Migrator.

If you have only one database, it does not make much difference which method of configuration you use.
However if you have a test and development databases locally (recommended), then you also have staging and
production environments, we certainly recommend using property file configuration over Maven profiles.


Here is a simple plugin element for the plugin:

~~~~{.xml}

<properties>
    <activejdbc.version>1.4.11</activejdbc.version>
    <environments>test,development</environments>
</properties>
<build>
    <plugin>
        <groupId>org.javalite</groupId>
        <artifactId>db-migrator-maven-plugin</artifactId>
        <version>${activejdbc.version}</version>
        <configuration>
            <configFile>${project.basedir}/src/main/resources/database.properties</configFile>
            <environments>${environments}</environments>
        </configuration>
        <executions>
            <execution>
                <id>dev_migrations</id>
                <phase>validate</phase>
                <goals>
                    <goal>migrate</goal>
                </goals>
            </execution>
        </executions>
        <dependencies>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>5.1.34</version>
            </dependency>
        </dependencies>
    </plugin>
</build>
~~~~

As you can see, the configuration is really located in file `${project.basedir}/src/main/resources/database.properties`.
The contents of this file might look lile this:

```
development.driver=com.mysql.jdbc.Driver
development.username=root
development.password=passwd
development.url=jdbc:mysql://localhost/jes_development

test.driver=com.mysql.jdbc.Driver
test.username=root
test.password=passwd
test.url=jdbc:mysql://localhost/jes_test

testenv.driver=com.mysql.jdbc.Driver
testenv.username=jes
testenv.password=passwd
testenv.url=jdbc:mysql://localhost/jes_testenv

staging.driver=com.mysql.jdbc.Driver
staging.username=jes
staging.password=passwd
staging.url=jdbc:mysql://192.168.80.40/jes_staging

production.driver=com.mysql.jdbc.Driver
production.username=jes
production.password=passwd
production.url=jdbc:mysql://192.168.20.40/jes_production

```

In the file above, the blocks of properties with a specific prefix belong to a corresponding environment.
For example, there are 5 environments defined on this file:

* development
* test
* testenv
* staging
* production

### Executing for environment

Executing DB-Migrator is described above in section [All goals](#all-goals).
The plugin will run migrations for environments `test` and `development` because they are configured in the
`<properties>` section (see above). In order to override this behavior, you need to override the `<environments>`
property from a command line like this:

```
mvn db-migrator:migrate -Denvironments=staging
```

The command above will run the goal `migrate` with a set of properties to point to staging environment.
It makes it easy to point the plugin to different databases and write simple scripts for migrations.

If you want to execute for multiple environments (typical example, is to migrate all local databases), simply 
 list environments as a comma separated list:

```
mvn db-migrator:migrate -Denvironments=test,development
```


### Property file location

In the example above, the file is located in project sources: `${project.basedir}/src/main/resources/database.properties`.
This means that it will probably be pushed to your source repository with credentials.
In some projects this is acceptable, while in others it is not. However, location of this file is irrelevant to the
plugin, so a development team can decide whether they want to commit it to a repository, or keep locally private.


## Maven configuration

Maven configuration is more complicated, and not recommended.
Please, see property file-based configuration above.

Here is an example of simple configuration:

~~~~ {.xml}
<plugin>
    <groupId>org.javalite</groupId>
    <artifactId>db-migrator-maven-plugin</artifactId>
    <version>1.4.11</version>
    <configuration>
        <driver>com.mysql.jdbc.Driver</driver>
        <url>jdbc:mysql://localhost/test_project</url>
        <username>your user</username>
        <password>your password</password>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.25</version>
        </dependency>
    </dependencies>
</plugin>
~~~~

In a more realistic project, you will have more than one database, such as test, development, production, etc.
In order to migrate multiple databases, use Maven executions:

First, configure the plugin in `pluginManagement`:

~~~~ {.xml}
<pluginManagement>
    <plugins>
        <plugin>
            <groupId>org.javalite</groupId>
            <artifactId>db-migrator-maven-plugin</artifactId>
            <version>1.4.11</version>
            <configuration>
                <username>${jdbc.user}</username>
                <password>${jdbc.password}</password>
                <driver>${jdbc.driver}</driver>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                    <version>5.1.25</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</pluginManagement>
~~~~

where user, password and driver are configured as project properties.

 After that, you can configure the plugin to execute multiple databases by adding many executions.
 Here is example of one execution:

~~~~ {.xml}
<plugin>
    <groupId>org.javalite</groupId>
    <artifactId>db-migrator-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>dev_migrations</id>
            <phase>validate</phase>
            <goals>
                <goal>migrate</goal>
            </goals>
        </execution>
        <execution>
            <id>test_migrations</id>
            <phase>validate</phase>
            <goals>
                <goal>migrate</goal>
            </goals>
            <configuration>
                <url>${jdbc.test.url}</url>
            </configuration>
        </execution>
    </executions>
</plugin>
~~~~

The plugin tied to a validate phase, which will ensure that it will migrate
schema at the very start of the build. Add more executions to run against multiple databases. You can use Maven profiles
with this plugin to migrate databases in different environments, such as production.


### Configuration properties

* `url` - JDBC connection URL
* `driver` - JDBC connection driver
* `username` - JDBC connection user name
* `password` - JDBC connection password
* `migrationsPath` - location of migration files, defaults to  `src/migrations/`
* `createSql` - create database SQL, defaults to `create database {$your database}`
* `dropSql` - drop database SQL, defaults to `drop database {$your database}`


### Maintaining multiple databases

You can use Maven profiles to maintain multiple database, as well as specific configuration for different executions
of the same plugin.

## Migration records

DB-Migrator maintains a record of executing migrations in table `SCHEMA_VERSION` and will not execute the same migration twice:

~~~~ {.prettyprint}
mysql> select * from schema_version;
~~~~

Results in the following output:

 version         applied_on          duration 
 --------------- ------------------  -----------
 20140302193112  2014-07-03 22:08:41 22 
 20140302193141  2014-07-03 22:08:41 12 
 20140303150340  2014-07-03 22:08:41 13 
 20140304173708  2014-07-03 22:08:41 20 
 20140304174236  2014-07-03 22:08:41 18 
 20140305235518  2014-07-03 22:08:41 13 
 20140306002924  2014-07-03 22:08:41 12 
 20140307192002  2014-07-03 22:08:41 25 
 20140309143448  2014-07-03 22:08:41 25 
 20140310141755  2014-07-03 22:08:41 25 


## Development process


Since all migrations are recorded as text (SQL) files, and contain a time stamp in the name, every time a developer pulls
sources from source repository and execute a build, your database is upgraded to the latest migration automatically.
In our experience, this reduced amount of attention we had to give a DB to a minimum. Basically a developer creates a
new migration and checks it in, which makes it propagate to other developer machines automatically.

### Step 1: Create migration file

At the root of your project execute: 

```
mvn db-migrator:new -Dname=create_people_table
```
This will simply create a new empty text file: 

```
Created new migration: .../src/migrations/20160130213201_create_people_table.sql
```

where 20160130213201 is a timestamp that is a good indicator when this migration was created. 

### Step 2: Write SQL:

Open this file with your favorite text editor and add free hand SQL there: 

```
CREATE TABLE people (
  id  int(11) DEFAULT NULL auto_increment PRIMARY KEY,
  first_name VARCHAR(128),
  last_name  VARCHAR(128),
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;
```

### Step 3: Run migration:

Execute this command and observe output: 

```
$mvn db-migrator:migrate
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building ActiveWeb Example WebApp 1.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- db-migrator-maven-plugin:1.4.11:migrate (default-cli) @ activeweb-simple ---
[INFO] Sourcing database configuration from file: /home/igor/projects/javalite/activeweb-simple/src/main/resources/database.properties
[INFO] Environment: test
[INFO] Migrating jdbc:mysql://localhost/simple_test using migrations at /home/igor/projects/javalite/activeweb-simple/src/migrations/
[INFO] Trying migrations at: /home/igor/projects/javalite/activeweb-simple/src/migrations 
[INFO] Migrating database, applying 1 migration(s)
[INFO] Running migration 20160130213201_create_person_table.sql
[INFO] CREATE TABLE people ( id  int(11) DEFAULT NULL auto_increment PRIMARY KEY, first_name VARCHAR(128), last_name  VARCHAR(128), created_at DATETIME, updated_at DATETIME )ENGINE=InnoDB
[INFO] Migrated database
[INFO] Environment: development
[INFO] Migrating jdbc:mysql://localhost/simple_development using migrations at /home/igor/projects/javalite/activeweb-simple/src/migrations/
[INFO] Trying migrations at: /home/igor/projects/javalite/activeweb-simple/src/migrations 
[INFO] Migrating database, applying 1 migration(s)
[INFO] Running migration 20160130213201_create_person_table.sql
[INFO] CREATE TABLE people ( id  int(11) DEFAULT NULL auto_increment PRIMARY KEY, first_name VARCHAR(128), last_name  VARCHAR(128), created_at DATETIME, updated_at DATETIME )ENGINE=InnoDB
[INFO] Migrated database

```

as you can see from above in this case. two databases were migrated: test and development. The output of the migration command is self-explanatory. 
If you had more migration files defined that have not yet been migrated, they all will be migrated by this step. 

### Step 3 alternative

Since the DB-Migrator is a Maven plugin and is executed during a normal build, every time you run a project build with:

```
mvn clean install
```

your new migrations will be executed against target databases. So, as such you do not need to execute 
`mvn db-migrator:migrate` during a normal development process. 