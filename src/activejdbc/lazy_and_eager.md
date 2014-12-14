<ol class=breadcrumb>
   <li><a href=/>Home</a></li>
   <li><a href=/activejdbc>ActiveJDBC</a></li>
   <li class=active>Lazy and eager</li>
</ol>
<div class=page-header>
   <h1>Lazy and eager <small></small></h1>
</div>



ActiveJDBC is lazy by default. In this sense, it has semantics closer to ActiveRecord than Hibernate.

## Lazy List

In a code like this:

~~~~ {.java  }
List<User> users = User.findAll(); // or User.where(".. query here");
for(User u: users){
    System.out.println(u);
}
~~~~

the list `users` is a type of [LazyList](http://javalite.github.io/activejdbc/org/javalite/activejdbc/LazyList.html).

Despite what it looks, the line 1. is not when the framework makes a call to the database. Only when the objects are
 queried from loop  on line 2, the framework pulls data from the database.


In fact, in this example:

~~~~ {.java  }
List<Employee> people = Employee.where("department = ? and hire_date > ? ", "IT", hireDate)
    .offset(21)
    .limit(10)
    .orderBy("hire_date asc");
~~~~

there is no access to database. All that is happening is that the [LazyList](http://javalite.github.io/activejdbc/org/javalite/activejdbc/LazyList.html)
 is progressively configured on lines 2, 3 and 4 in order to build a correct SQL query when the objects are requested from the list.


## Lazy dependencies

If you have a model User and a model Address, and they have a one to many relationship, when a
user has many addresses, the code:

~~~~ {.java}
User u = User.findById(1);
~~~~

does not load the associated addresses. Only when you call the getter for addresses, a query is generated and executed against DB:

~~~~ {.java}
List<Address> addresses = u.getAll(Address.class);
~~~~

In the example above, the collection of addresses is not cached in the User model, and a query is executed against a
DB as many times as this getter is called.

> ActiveJDBC uses a pass-through model. It means that the models do not cache relationships. Even after a call to
> get related objects the parent model does not retain a reference to them.

## Improve efficiency with eager loading

Let's consider an example where an ORM could unexpectedly generate a huge number of inefficient queries:

~~~~ {.java  }
List<Address> addresses = Address.findAll();

for(Address address: addresses){
   User user = address.parent(User.class);
   System.out.println(user);
}
~~~~

In the above example, the number of queries generated and executed is going to be N + 1, were N is a number of
addresses. This is because the first query is to get all addresses, and then for each address, there is a new query
to get a user parent (line 4).

This approach is going to kill performance in some applications. A better way is to load all parents at once by a single query:

~~~~ {.java}
List<Address> addresses = Address.findAll().include(User.class);

for(Address address: addresses){
   User user = address.parent(User.class);
   System.out.println(user);
}
~~~~

ActiveJDBC will then issue two queries: one to get all Addresses and the other to get all corresponding Users for each address.

The same logic can be applied to all relationships **going up and down**: one-to-many, many-to-one and many-to-many.

> Relationships that were loaded by `include()` *are cached* and will be returned each time (without further access to DB)
> when the same getter is used!

## Eager simultaneous loading of parents and children

Suppose we have two one to many relationships: Author has many Posts and a Post has many Comments. In cases like these,
we can load a post and all corresponding Authors and Comments very efficiently:

~~~~ {.java}
List<Post> todayPosts = Post.where("post_date = ?", today).include(Author.class, Comment.class);
~~~~

The above code will generate only three queries to DB, one per each table. This of course will create an object graph
in memory with certain implications. While it is going to be a more efficient approach from the point of DB IO view,
it certainly will consume more memory. Developers will need to understand the implications and perform test cases to
see if eager loading is improving or degrading performance.

## Conversion to Maps

When a model with included children is converted to a map, all the dependencies are converted to maps and inserted
into a parent model map too. Here is an example:

~~~~ {.java  }
LazyList<User> users = User.findAll().include(Address.class);
List<Map> maps = users.toMaps();

Map user = maps.get(0);

List<Map> addresses = (List<Map>)user.get("addresses");
~~~~

In the example above, on line 1 a list of users is requested from a DB, and this list is to include corresponding
addresses for each user. So far, this is the same as previous examples. However, on line 2 the users are converted
to a list of maps. When this happens, each map that was generated from a user model also contains a list of maps each
representing an address as a child of that user. What is more, is that the list of addresses is keyed from a user map
by a string "addresses" as evident on line 6. The key in each case like this is an interpolation of a name of a
child model to plural form according to the rules of the English language, which resulted in "addresses" in this case.

The same logic applies to many-to-one and many-to-many relationships.
