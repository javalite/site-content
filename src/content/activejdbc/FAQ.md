Title: ActiveJDBC FAQ

-   [What the heck is instrumentation?](#What_the_heck_is_instrumentation_)
-   [How to integrate AJ projects into Netbeans?](#How_to_integrate_AJ_projects_into_Netbeans_)
-   [Does it support database pools?](#Does_it_support_database_pools_)
-   [How to use Instrumentation plugin with Eclipse?](#How_to_use_Instrumentation_plugin_with_Eclipse_)
-   [Does ActiveJDBC support SQL IN statement?](#Does_ActiveJDBC_support_SQL_IN_statement_)

What the heck is instrumentation?
=================================

See this page: [Instrumentation](Instrumentation)

How to integrate AJ projects into Netbeans?
===========================================

Look here: [NetbeansIntegration](NetbeansIntegration)

Does it support database pools?
===============================

YES, please see here: [Database connection pools](http://code.google.com/p/activejdbc/wiki/DatabaseConnectionManagement#Database_connection_pools), and also see example of pool usage: [C3P0PoolTest](http://code.google.com/p/activejdbc/source/browse/trunk/activejdbc/src/test/java/activejdbc/C3P0PoolTest.java)

How to use Instrumentation plugin with Eclipse?
===============================================

Please, take a look at this thread: https://groups.google.com/forum/?fromgroups=\#!searchin/activejdbc-group/eclipse/activejdbc-group/xQ5gUSnCalc/ZuuPrHsriuAJ

Does ActiveJDBC support SQL IN statement?
=========================================

This is one not supported by JDBC: http://stackoverflow.com/questions/178479/preparedstatement-in-clause-alternatives

However, there are workaround alternatives, most of them centered around concatenating strings. Basically you need to construct a full query as string:

` String sql = "name IN ('John', 'Hellen', 'Henry')";`

To make this, you can do: ` String sql = String.format("name IN ('%s')", join(list("John", "Hellen", "Henry"), "', '"); long  count = Person.count(sql);`

where `join()` method can be statically imported from: http://ipsolutionsdev.com/activejdbc/org/javalite/common/Util.html\#join%28java.util.Collection,%20java.lang.String%29

and `list()` method can be statically imported from: http://ipsolutionsdev.com/activejdbc/org/javalite/common/Collections.html\#list%28T...%29 both of these classes are already on your classpath.
