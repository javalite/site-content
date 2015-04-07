<ol class=breadcrumb>
   <li><a href=/>JavaLite</a></li>
   <li><a href=/activejdbc>ActiveJDBC</a></li>
   <li class=active>English inflections</li>
</ol>
<div class=page-header>
   <h1>English inflections <small></small></h1>
</div>




## Table vs Model name

By convention, all tables use plural form for a name, while corresponding models use singular form.

Examples are:

* Table `COMPUTER` is mapped to model `Computer`
* Table `PEOPLE` is mapped to model `Person`
* etc.

> This convention can be overridden with `@Table` annotation.


## English inflections

ActiveJDBC is using English inflections to convert singular form of a model to a plural form of a table.
It does include simple cases such as adding an "s" at the end: `COMPUTER` => `Computer`, as well
 as English exceptions, such as `PEOPLE` => `Person`, `MICE` => `Mouse`, etc.

> If you are interested in how this is done, and what exceptions are supported, you can inspect source code
of [Inflector](https://github.com/javalite/activejdbc/blob/master/javalite-common/src/main/java/org/javalite/common/Inflector.java)

## Bootstrapping

At the time of first use of a model after an application start, ActiveJDBC fetches metadata from a  connected database
 and looks for tables corresponding to names of models using either English inflections, or values provided by `@Table`
 annotations

Examples:

### Default mapping

~~~~ {.java}
public class Computer{}
~~~~

will be mapped to a table `COMPUTERS`.


### Default mapping with English exception

~~~~ {.java}
public class Octopus{}
~~~~

will be mapped to a table `OCTOPI`.


### Overridden mapping

~~~~ {.java}
@Table("Operator")
public class Operator{}
~~~~

will be mapped to a table `OPERATOR`.




