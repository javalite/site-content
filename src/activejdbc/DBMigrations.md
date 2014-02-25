Title: How to perform DB migrations

-   [Introduction](#Introduction)
-   [Configuration of Carbon 5](#Configuration_of_Carbon_5)
-   [Create migration](#Create_migration)
-   [Development process](#Development_process)
-   [Maintaining multiple databases](#Maintaining_multiple_databases)

Introduction
============

A DB migration is a step for "developing" your database. This means every time you need a new table, column, or need to remove something from DB, you write a DB "migration". In the Rails world, the migrations are written in Ruby: [http://guides.rubyonrails.org/migrations.html](http://guides.rubyonrails.org/migrations.html), but I feel that migrations ought to be written in a native to the DB language. This will achieve two goals:

-   developers do not need to learn yet another language and conventions
-   DBAs can help tweak migration code.

After surveying a number of Java projects for DB migrations, we decided to use Carbon 5 project [c5-db-migration](http://code.google.com/p/c5-db-migration/). Carbon 5 is very close to the idea of migrations, has a good community and support, but most of all, on all of our MySQL and Oracle projects we never had issues with it and never needed support.

Configuration of Carbon 5
=========================

There is nothing special about configuring Carbon 5 Maven plugin that is specific to ActiveJDBC, and you can easily follow their guide: [Carbon 5 Maven Plugin](http://code.google.com/p/c5-db-migration/wiki/MavenPlugin), however you can see how this is configured for a Kitchensink project for ActiveWeb: [http://code.google.com/p/activeweb/source/browse/trunk/kitchensink/pom.xml](http://code.google.com/p/activeweb/source/browse/trunk/kitchensink/pom.xml)

Basically the simplest-most configuration would be:

~~~~ {.prettyprint}
<plugin>
    <groupId>com.carbonfive.db-support</groupId>
    <artifactId>db-migration-maven-plugin</artifactId>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.0.4</version>
        </dependency>
    </dependencies>
    <configuration>
        <url>your value here</url>
        <username>your value here</username>
        <password>your value here</password>
        <driver>your value here</driver>
    </configuration>
    <executions>
        <execution>
            <id>dev_migrations</id>
            <goals>
                <goal>migrate</goal>
            </goals>
            <phase>validate</phase>
        </execution>
    </executions>
</plugin>
~~~~

Create migration
================

To create a new migration, execute this command:

~~~~ {.prettyprint}
mvn db-migration:new -Dname=create_person_table
~~~~

this action would create an empty file:

~~~~ {.prettyprint}
./src/main/db/migrations/20101112230703_create_person_table.sql
~~~~

After that, add this code to the file:

~~~~ {.prettyprint}
CREATE TABLE people (
  id  int(11) DEFAULT NULL auto_increment PRIMARY KEY,
  first_name VARCHAR(128),
  last_name VARCHAR(128),
  created_at DATETIME,
  updated_at DATETIME
)TYPE=InnoDB;;
~~~~

and you have a migration. To push this migration to the DB, you can execute this command:

~~~~ {.prettyprint}
mvn validate
~~~~

this is because if you look at configuration of Maven plugin, you will see that it is bound to the "verify" life cycle. If you want, you can bind it to anther life cycle.

Carbon 5 maintains acts of executing migrations in table SCHEMA\_VERSION and will not execute the same migration twice:

~~~~ {.prettyprint}
mysql> select * from schema_version;
+----------------+---------------------+----------+
| version        | applied_on          | duration |
+----------------+---------------------+----------+
| 20100909211252 | 2011-03-01 20:18:22 |       38 |
| 20100910235216 | 2011-03-01 20:18:22 |       30 |
| 20100914234548 | 2011-03-01 20:18:22 |       19 |
| 20100927225020 | 2011-03-01 20:18:22 |       17 |
| 20101002065645 | 2011-03-01 20:18:22 |       23 |
| 20101013000334 | 2011-03-01 20:18:22 |       89 |
| 20101013102224 | 2011-03-01 20:18:22 |        9 |
| 20101028000756 | 2011-03-01 20:18:22 |        5 |
| 20101031010628 | 2011-03-01 20:18:22 |       16 |
| 20110414035138 | 2011-04-13 23:03:42 |        8 |
+----------------+---------------------+----------+
~~~~

Development process
===================

Since all migrations are recorded as text (SQL) files, and contain a time stamp in the name, every time you update sources from source repository and execute a build, your database is upgraded to the latest migration automatically. In our experience, this reduced amount of attention we had to give a DB to a minimum. Basically a developer creates a new migration and checks it in, which makes it propagate to other developer machines automatically.

Maintaining multiple databases
==============================

You can use Maven profiles to maintain multiple database. An example of such configuration is a Kitchensink project from ActiveWeb: [http://code.google.com/p/activeweb/source/browse/trunk/kitchensink/pom.xml](http://code.google.com/p/activeweb/source/browse/trunk/kitchensink/pom.xml) this should be self-explanatory.
