
# Caching




Caching is an integral part of every major system, It improves performance, reduces IO and makes overall user
experience more pleasurable. Caching in ActiveJDBC works on the level of query and creation of model instances.
For instance, the call:

~~~~ {.java}
List<Library> illLibs = Library.where("state = ?", "IL");
~~~~

might call into DB, or a result can come from cache, depending how cache and specifically model `Library` was configured

## Cache annotation

ActiveJDBC provides annotation to specify queries against which tables will be cached:

~~~~ {.java}
@Cached
public class Library extends Model {}
~~~~

As in other cases, this is a declaration that marks a model as "cachable". If you enable logging (by providing a system property `activejdbc.log`), you will see extensive output from ActiveJDBC, similar to this:

~~~~ {.java}
3076 [main] INFO org.javalite.activejdbc.DB - Query: "SELECT * FROM libraries WHERE id = ?", with parameters: [1], took: 0 milliseconds
3076 [main] INFO org.javalite.activejdbc.cache.QueryCache - HIT, "SELECT * FROM libraries WHERE id = ?", with parameters: [1]
3077 [main] INFO org.javalite.activejdbc.DB - Query: "INSERT INTO libraries (address, state, city) VALUES (?, ?, ?)", with parameters: [123 Pirate Street, CA, Bloomington], took: 1 milliseconds
3077 [main] INFO org.javalite.activejdbc.cache.QueryCache - table cache purged for: libraries
3077 [main] INFO org.javalite.activejdbc.cache.QueryCache - table cache purged for: books
3077 [main] INFO org.javalite.activejdbc.cache.QueryCache - MISS, "SELECT * FROM libraries WHERE id = ?", with parameters: [1]
3078 [main] INFO org.javalite.activejdbc.DB - Query: "SELECT * FROM libraries WHERE id = ?", with parameters: [1], took: 0 milliseconds
~~~~

## Cache Configuration (AJ Version 1.1 and above)

The new cache configuration includes providing a cache manager class name in the file `activejdbc.properties`.
This file will have to be on the root of classpath. Here is one example:

~~~~ {.prettyprint}
#inside file: activejdbc.properties
#or EHCache:
cache.manager=org.javalite.activejdbc.cache.EHCacheManager
# use OSCache
#cache.manager=org.javalite.activejdbc.cache.OSCacheManager
~~~~

Here two things happen: 1. Cache in general is enabled (it is not enabled even if you have @Cached annotations on classes),
and 2. AJ will be using EHCacheManager as implementation of cache.

## Automatic cache purging

If you examine the log from above, you will see that after an insert statement into the "LIBRARIES" table,
the system is purging cache related to this table, as well as "BOOKS" table. ActiveJDBC does this since the cache in
memory might be potentially of out sync with the data in the DB, and hence will be purged. Related tables' caches are
also purged. Since there exists relationship: library has many books, the books cache could also be stale, and this is
a reason why a table "BOOKS" purged as well.

## Manual cache purging


If you want to manually purge caches (in cases you make destructive data operations outside Model API), you can do so:

~~~~ {.java}
org.javalite.activejdbc.cache.QueryCache.instance().purgeTableCache("books");
~~~~

or:

~~~~ {.java}
Books.purgeCache();
~~~~

## What to cache

While caching is a complex issue, I can suggest caching predominantly lookup data. Lookup data is something that does
not change very frequently. If you start caching everything, you might run into a problem of cache thrashing where you
fill cache with data, and purge it soon after, without having a benefit of caching. Instead of improving performance,
you will degrade it with extra CPU, RAM and IO (is cluster is configured) used and little or no benefit of having a
cache in the first place.

## Things to be careful about

### Cached data is exposed directly 

When retrieving instances of cached models, be aware that exactly the same instances can be returned by subsequent calls to the same query. ActiveJDBC, as a lightweight framework, won't try to be "intelligent" and manage clones of cached data for you. So, for example, considering `Person` is annotated as `@Cached`, two subsequent calls to `Person.findById(1)` will return the same instance:

~~~~ {.java}
Person p1 = Person.findById(1);
System.out.println(p1.get("name")); // prints: John

p1.set("name", "Jane"); // changes the cached data directly
// don't save p1, and ...

Person p2 = Person.findById(1); // ... find the same person again
System.out.println(p2.get("name")); // prints: Jane
~~~~

### Either caching or optimistic locking

Caching and [optimistic_locking](optimistic_locking) don't get along. **Don't use both together.**

Caching guarantees the result of subsequent calls to the same query return the same instances. So there can't be different versions of the same result set living in the memory shared by the cache manager. Suppose `Profile`, a model with [optimistic_locking](optimistic_locking#when-collisions-happen), is also annotated as `@Cached`. The following will happen:

~~~~ {.java}
Profile p1 = Profile.findById(1);
Profile p2 = Profile.findById(1);
// p1 and p2 are actually references to the same instance.

p1.set("profile_type", "hotel");
p1.saveIt();
// record_version of the instance is incremented, then updated in the database.

p2.set("profile_type", "vacation");
p2.saveIt();
// As this is the same instance that had record_version incremented ealier,
// its record_version value will match the database
// and no StaleModelException will be thrown.
~~~~
 
### Relationships

ActiveJDBC manages caches for models and their respective relationships (read above), but in some cases you will use a query that ties together unrelated models:

~~~~ {.java}
List<User> users = User.where("id not in (select user_id from restricted_users)");
~~~~

If there exists a model User that is cached, and model RestrictedUser, and these tables/models have no relationship, then the line above could present a logical problem. If you execute the line above, and later change content of RESTRICTED\_USERS table, then the query above will not see the change, and will return stale data. Developers need to be aware of this, and deal with these issues carefully. Whenever you change data in RESTRICTED\_USERS table, please purge User model:

~~~~ {.java}
User.purgeCache();
~~~~

## Cache providers

ActiveJDBC has a simple plugin framework for adding cache providers. Currently supports:

-   OSCache is dead now. Although it is working just fine on many of our projects, we recommend using EHCache
-   [EHCache](http://ehcache.org/). EHCache is high performance popular open source project. For documentation, please refer to: [http://ehcache.org/documentation](http://ehcache.org/documentation)

## EHCache configuration

Configuration needs to be provided in a file called `ehcache.xml` found at the root of a classpath. Example of a file content:

~~~~ {.xml}
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

Please, note that ActiveJDBC does not create named caches in EHCache, but only uses default configuration specified by
`defaultCache` element in this file.

### EHCache clustering with Terracotta

The EHCache project has excellent documentation found here:
[http://ehcache.org/documentation](http://ehcache.org/documentation).
Adding clustering support to EHCache is somewhat simple, you have to add a `terracottaConfig` to the `ehcache.xml` file.
For more information, please refer to the EHCache documentation.
