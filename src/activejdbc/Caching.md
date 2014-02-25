Title: ActiveJDBC Caching

[Introduction](#Introduction)

[Cache annotation](#Cache_annotation)

[Cache Configuration (AJ Version 1.1 and above)](#Cache_Configuration__AJ_Version_1_1_and_above_)

[Cache Configuration (AJ Version 1.0 and below)](#Cache_Configuration__AJ_Version_1_0_and_below_)

[Automatic cache purging](#Automatic_cache_purging)

[Manual cache purging](#Manual_cache_purging)

[What to cache](#What_to_cache)

[Things to be careful about](#Things_to_be_careful_about)

[Cache providers](#Cache_providers)

[EHCache configuration](#EHCache_configuration)

-   [EHCache clustering with Terracotta](#EHCache_clustering_with_Terracotta)

Introduction
============

Caching is an integral part of every major system, It improves performance, reduces IO and makes overall user experience more pleasurable. Caching in ActiveJDBC works on the level of query and creation of model instances. For instance, the call:

~~~~ {.prettyprint}
List<Library> illLibs = Library.where("state = ?", "IL");
~~~~

might call into DB, or a result can come from cache, depending how cache and specifically model `Library` was configured

Cache annotation
================

ActiveJDBC provides annotation to specify queries against which tables will be cached:

~~~~ {.prettyprint}
@Cached
public class Library extends Model {}
~~~~

As in other cases, this is a declaration that marks a model as "cachable". If you enable logging (by providing a system property `activejdbc.log`), you will see extensive output from ActiveJDBC, similar to this:

~~~~ {.prettyprint}
3076 [main] INFO activejdbc.DB - Query: "SELECT * FROM libraries WHERE id = ?", with parameters: [1], took: 0 milliseconds
3076 [main] INFO activejdbc.cache.QueryCache - HIT, "SELECT * FROM libraries WHERE id = ?", with parameters: [1]
3077 [main] INFO activejdbc.DB - Query: "INSERT INTO libraries (address, state, city) VALUES (?, ?, ?)", with parameters: [123 Pirate Street, CA, Bloomington], took: 1 milliseconds
3077 [main] INFO activejdbc.cache.QueryCache - table cache purged for: libraries
3077 [main] INFO activejdbc.cache.QueryCache - table cache purged for: books
3077 [main] INFO activejdbc.cache.QueryCache - MISS, "SELECT * FROM libraries WHERE id = ?", with parameters: [1]
3078 [main] INFO activejdbc.DB - Query: "SELECT * FROM libraries WHERE id = ?", with parameters: [1], took: 0 milliseconds
~~~~

Cache Configuration (AJ Version 1.1 and above)
==============================================

The new cache configuration includes providing a cache manager class name in the file `activejdbc.properties`. This file will have to be on the root of classpath. Here is one example:

~~~~ {.prettyprint}
#inside file: activejdbc.properties
# use OSCache
cache.manager=org.javalite.activejdbc.cache.OSCacheManager
#or EHCache:
#cache.manager=org.javalite.activejdbc.cache.EHCacheManager
~~~~

Here two things happen: 1. Cache in general is enabled (it is not enabled even if you have @Cached annotations on classes), and 2. AJ will be using OSCacheManager as implementation of cache.

Cache Configuration (AJ Version 1.0 and below)
==============================================

In order to enable caching (even if you have @Cache annotations!) you have to have a property file on the classpath called `activejdbc.properties`. This file needs to have one line of code:

~~~~ {.prettyprint}
#inside file: activejdbc.properties
cache.enabled=true
~~~~

Without this file with one property no caching will be turned on.

Automatic cache purging
=======================

If you examine the log from above, you will see that after an insert statement into the "LIBRARIES" table, the system is purging cache related to this table, as well as "BOOKS" table. ActiveJDBC does this since the cache in memory might be potentially of out sync with the data in the DB, and hence will be purged. Related tables' caches are also purged. Since there exists relationship: library has many books, the books cache could also be stale, and this is a reason why a table "BOOKS" purged as well.

Manual cache purging
====================

If you want to manually purge caches (in cases you make destructive data operations outside Model API), you can do so:

~~~~ {.prettyprint}
activejdbc.cache.QueryCache.instance().purgeTableCache("books");
~~~~

or:

~~~~ {.prettyprint}
Books.purgeCache();
~~~~

What to cache
=============

While caching is a complex issue, I can suggest caching predominantly lookup data. Lookup data is something that does not change very frequently. If you start caching everything, you might run into a problem of cache thrashing where you fill cache with data, and purge it soon after, without having a benefit of caching. Instead of improving performance, you will degrade it with extra CPU, RAM and IO (is cluster is configured) used and little or no benefit of having a cache in the first place.

Things to be careful about
==========================

ActiveJDBC manages caches for models and their respective relationships (read above), but in some cases you will use a query that ties together unrelated models:

~~~~ {.prettyprint}
List<User> users = User.where("id not in (select user_id from restricted_users)");
~~~~

If there exists a model User that is cached, and model RestrictedUser, and these tables/models have no relationship, then the line above could present a logical problem. If you execute the line above, and later change content of RESTRICTED\_USERS table, then the query above will not see the change, and will return stale data. Developers need to be aware of this, and deal with these issues carefully. Whenever you change data in RESTRICTED\_USERS table, please purge User model:

~~~~ {.prettyprint}
User.purgeCache();
~~~~

Cache providers
===============

ActiveJDBC has a simple plugin framework for adding cache providers. Currently supports:

-   OSCache is dead now. Although it is working just fine on many of our projects, we recommend using EHCache
-   [EHCache](http://ehcache.org/). EHCache is high performance popular open source project. For documentation, please refer to: [http://ehcache.org/documentation](http://ehcache.org/documentation)

EHCache configuration
=====================

Configuration needs to be provided in a file called `ehcache.xml` found at the root of a classpath. Example of a file content:

~~~~ {.prettyprint}
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="true" monitoring="autodetect">

    <diskStore path="java.io.tmpdir"/>
    <defaultCache
            maxElementsInMemory="1000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            overflowToDisk="true"
            maxElementsOnDisk="10000"
            diskPersistent="false"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU"
            />
</ehcache>
~~~~

Please, note that ActiveJDBC does creates named caches in EHCache, but only uses default configuration specified by `defaultCache` element in this file.

EHCache clustering with Terracotta
----------------------------------

The EHCache project has excellent documentation found here: [http://ehcache.org/documentation/terracotta/configuration](http://ehcache.org/documentation/terracotta/configuration). Adding clustering support to EHCache is somewhat simple, you have to add a `terracottaConfig` to the `ehcache.xml` file. For more information, please refer to the EHCache documentation.

Back to [Features](Features)
