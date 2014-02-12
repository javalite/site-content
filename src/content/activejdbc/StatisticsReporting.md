Title: ActiveJDBC collects statistics on running queries

Introduction
============

ActiveJDBC can report on times queries are taking to execute. If statistics collection is enabled, then AJ will start accumulating times it takes to execute various queries. This information can help identify bottlenecks in application performance.

Configuring ActiveJDBC
======================

In order to configure ActiveJDBC to collect statistical data on queries, you need to add one property to `activejdbc.properties` file:

~~~~ {.prettyprint}
collectStatistics = true
~~~~

On the next start of the application, ActiveJDBC will collect statistical data on queries.

How to get queries execution times
==================================

The API to get statistical data is:

~~~~ {.prettyprint}
List<QueryStats>  statistics = Registry.instance().getStatisticsQueue().getReportSortedBy("avg");
~~~~

Where argument to `getReportSortedBy(...)` methods can be one of: "total", "avg", "min", "max", "count" and this indicates how you want to sort the results (the values are self-explanatory). The `QueryStats` object is a simple bean which contains the query text, as well as values: "total", "avg", "min", "max", "count".

How to generate non - ActiveJDBC statistics report
==================================================

In some cases, you might want to accumulate statistical information for actions in your application which have nothing to do with ActiveJDBC. You can then perform this operation:

~~~~ {.prettyprint}
Registry.instance().getStatisticsQueue().enqueue(new QueryExecutionEvent(query, System.currentTimeMillis() - start));
~~~~

Where "query" is your custom operation, and "start" was a time before it started. The statistical information for your operation will then be accumulated and available for reporting just as any other ActiveJDBC query.

Back to [Features](Features)
