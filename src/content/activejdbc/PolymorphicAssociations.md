Title: How to use and what are polymorphic associations

[Introduction](#Introduction)

[Operations on polymorphic associations](#Operations_on_polymorphic_associations)

-   [Adding and searching for polymorphic children](#Adding_and_searching_for_polymorphic_children)
-   [Conditional search for polymorphic children](#Conditional_search_for_polymorphic_children)
-   [Removing polymorphic children](#Removing_polymorphic_children)
-   [Deleting polymorphic parents](#Deleting_polymorphic_parents)
-   [Finding polymorphic parent](#Finding_polymorphic_parent)

[Override standard parent type values](#Override_standard_parent_type_values)

[Conclusion](#Conclusion)

Introduction
============

ActiveJDBC polymorphic associations are similar to ActiveRecord polymorphic associations. You can use PM in cases when you have several one to many relationships that you could aggregate because for all the parents, the children are similar.

For example, imagine that the entities in your system that will need to be tagged. For instance, you have products and you have reviews. Both will need to be tagged. A naive implementation would be to create two one to many relationships:

-   Product has many ProductTag(s)
-   Review has many ReviewTag(s)

This will work.. kind of, but this simplified approach will violate a DRY principle because the `PRODUCT_TAG` and `REVIEW_TAG` tables will be identical (except for names!).

A better approach would be to use Polymorphic associations. In the PM, you would create one table called 'TAG', and add two columns to this table, besides the ones that you need:

-   `PARENT_ID`
-   `PARENT_TYPE`

After this, you will need to provide one last bit of information to the framework by specifying relationships:

~~~~ {.prettyprint}

public class Product extends Model{}

public class Review extends Model{}

@BelongsToPolymorphic(parents = {Product.class, Review.class})
public class Tag extends Model{}
~~~~

This annotation tells ActiveJDBC that Product has many Tags and Review has many tags. The annotation itself is easy to understand if you read it aloud.

Once the setup is done, you can proceed to use the models as normal one to many associations:

Operations on polymorphic associations
======================================

Operations themselves are no different from regular ActiveJDBC one to many operations:

Adding and searching for polymorphic children
---------------------------------------------

~~~~ {.prettyprint}
Product p =  Product.findById(100);
p.add(Tag.create("tag", "basket"));
p.add(Tag.create("tag", "toy"));
List<Tag> tags = p.getAll(Tag.class);
...iterate

Review customerReview =  Review.findById(2024);
customerReview.add(Tag.create("tag", "fun"));
customerReview.add(Tag.create("tag", "useful"));
List<Tag> tags = customerReview.getAll(Tag.class);
... iterate
~~~~

The table TAG content might look like this after operations above:

~~~~ {.prettyprint}
+----+---------+--------------------------------+
| id | tag     | parent_id | parent_type        |
+----+------------+-----------------------------+
|  1 | toy     |       100 | com.acme.Product   | 
|  2 | basket  |       100 | com.acme.Product   | 
|  3 | fun     |      2024 | com.acme.Review    | 
|  4 | useful  |      2024 | com.acme.Review    | 
+----+---------+--------------------------------+
~~~~

Conditional search for polymorphic children
-------------------------------------------

While the `getAll(type)` method returns all relations, the `get(type)` method allows for a selection criteria on the child table:

~~~~ {.prettyprint}
List<Tag> tags = product.get(Tag.class, "tag = ?", "toy");
~~~~

Removing polymorphic children
-----------------------------

Removing children is as easy as expected;

~~~~ {.prettyprint}
Product toyBasket =  Product.findById(100);
Tag t = Tag.findById(1);
toyBasket.remove(t);
~~~~

Deleting polymorphic parents
----------------------------

When deleting a record that is a parent to polymorphic children, you have two options:

-   Only delete the parent itself. This will leave orphan children:

~~~~ {.prettyprint}
toyBasket.delete();
~~~~

-   Delete parent along with all the children:

~~~~ {.prettyprint}
toyBasket.deleteCascade(); // or toyBasket.delete(true);
~~~~

The latter will delete the parent along with all associated polymorphic children.

Finding polymorphic parent
--------------------------

ActiveJDBC also provides a way to navigate from children to parents in relationships:

~~~~ {.prettyprint}
Tag t = Tag.findById(1);
Product p = t.parent(Product.class);
...
~~~~

Override standard parent type values
====================================

In some cases, it is not possible to have a fully qualified class name in the "parent\_type" column. This is usually a case when the same table backs a different ORM which also supports polymorphic associations (ActiveRecord for example).

When faced with this problem, you can use annotation to override default behavior:

~~~~ {.prettyprint}
@BelongsToPolymorphic(
parents     = { Vehicle.class, Mammal.class}, 
typeLabels  = {"Vehicle",     "Mammal"} )
public class Classification extends Model {}
~~~~

This defines polymorphic associations between models Classification, Mammal and Vehicle, such that the "parent\_type" column of CLASSIFICATIONS table will contain values "Vehicle" and "Mammal" for corresponding parent records from VEHICLE and MAMMAL tables. The order of parent classes and type labels is important, they must correspond to one another.

Here is an example of CLASSIFICATIONS table:

~~~~ {.prettyprint}
+----+---------------+---------------------------+
| id | name          | parent_id | parent_type   |
+----+---------------+---------------------------+
|  1 | four wheeled  |       100 | Vehicle       | 
|  2 | sedan         |        23 | Vehicle       | 
|  3 | four legged   |      2024 | Mammal        | 
|  4 | furry         |      2023 | Mammal        | 
+----+------=--------+-----------+---------------+
~~~~

Conclusion
==========

Polymorphic association is a simple mechanism, but the provide a nice pattern for cases that happen much too often in projects. Setting them up and using with ActiveJDBC is so trivial, even a cavemen can do it :)

Back to [Features](Features).
