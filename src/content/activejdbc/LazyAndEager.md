Title: How to use eager loading with ActiveJDBC

-   [Lazy by default](#Lazy_by_default)
-   [Improve efficiency with eager loading](#Improve_efficiency_with_eager_loading)
-   [Eager simultaneous loading of parents and children](#Eager_simultaneous_loading_of_parents_and_children)
-   [Conversion to Maps](#Conversion_to_Maps)

Lazy by default
===============

ActiveJDBC is lazy by default. In this sense, it has semantics closer to ActiveRecord than Hibernate.

This means that if you have a model User and a model Address, and they have a one to many relationship, when a user has many addresses, the code:

~~~~ {.prettyprint}

User u = User.findById(1);
~~~~

does not load the associated addresses. Only when you call the getter for addresses, a query is generated and executed against DB:

~~~~ {.prettyprint}
List<Address> addresses = u.getAll(Address.class);
~~~~

In the example above, the collection of addresses is not cached in the User model, and a query is executed against a DB as many times as this getter is called.

Improve efficiency with eager loading
=====================================

Let's consider an example where an ORM could unexpectedly generate a huge number of inefficient queries:

~~~~ {.prettyprint}
1. List<Address> addresses = Address.findAll();
2. 
3. for(Address address: addresses){
4.    User user = address.parent(User.class);
5.    System.out.println(user);
6. }
~~~~

In the above example, the number of queries generated and executed is going to be N + 1, were N is a number of addresses. This is because the first query is to get all addresses, and then for each address, there is a new query to get a user parent (line 4).

This approach is going to kill performance in some applications. A better approach is to load all parents at once by a single query:

~~~~ {.prettyprint}
1. List<Address> addresses = Address.findAll().include(User.class);
2. 
3. for(Address address: addresses){
4.    User user = address.parent(User.class);
5.    System.out.println(user);
6. }
~~~~

The ActiveJDBC will then issue two queries: one to get all Address(es) and the other to get all corresponding User(s).

The same logic can be applied to all relationships going up and down: one to many, many to one and many to many.

NOTE: the relationships that were loaded by "include" are cached and will be returned each time (without further access to DB) when the same getter is used!

Eager simultaneous loading of parents and children
==================================================

Suppose we have two one to many relationships: Author has many Posts and a Post has many Comments. In cases like these, we can load a post and all corresponding Authors and Comments very efficiently:

~~~~ {.prettyprint}
List<Post> todayPosts = Post.where("post_date = ?", today).include(Author.class, Comment.class);
~~~~

The above code will generate only three queries to DB, one per each table. This of course will create an object graph in memory with certain implications. While it is going to be a more efficient approach from the point of DB IO view, it certainly will consume more RAM. Developers will need to understand the implications and perform test cases to see if eager loading is improving or degrading performance.

Conversion to Maps
==================

When a model with included children is converted to a map, all the dependencies are converted to maps and inserted into a parent model map too. Here is an example:

~~~~ {.prettyprint}
1.        LazyList<User> users = User.findAll().include(Address.class);
2.        List<Map> maps = users.toMaps();
3. 
4.        Map user = maps.get(0);
5. 
6.        List<Map> addresses = (List<Map>)user.get("addresses");
~~~~

In the example above, on line 1 a list of users is requested from a DB, and this list is to include corresponding addresses for each user. So far, this is the same as previous examples. However, on line 2 the users are converted to a list of maps. When this happens, each map that was generated from a user model also contains a list of maps each representing an address as a child of that user. What is more, is that the list of addresses is keyed from a user map by a string "addresses" as evident on line 6. The key in each case like this is an interpolation of a name of a child models to plural form according to the rules of the English language, which resulted in "addresses" in this case.

The same logic applies to many to one and many to many relationships.

Back to [Features](Features)
